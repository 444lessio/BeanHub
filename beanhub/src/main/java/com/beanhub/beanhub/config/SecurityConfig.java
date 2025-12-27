package com.beanhub.beanhub.config;

import com.beanhub.beanhub.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            com.beanhub.beanhub.model.User user = userRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            // Gestione ruoli (aggiunge ROLE_ se manca)
            String role = user.getRole().toUpperCase();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(role)
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. SBLOCCO TOTALE PER H2 CONSOLE
                .requestMatchers("/h2-console/**").permitAll()
                
                // Risorse statiche e Login
                .requestMatchers("/System_manager/login", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Area Manager accessibile a ADMIN e MANAGER
                .requestMatchers("/System_manager/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                
                // Tutto il resto è libero (o cambialo se vuoi restrizioni)
                .anyRequest().permitAll()
            )
            // 2. DISABILITA PROTEZIONI CHE ROMPONO H2
            .csrf(csrf -> csrf.disable()) // H2 non ha il token CSRF
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 usa i frame
            
            .formLogin(form -> form
                .loginPage("/System_manager/login")
                .defaultSuccessUrl("/System_manager/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/System_manager/logout")
                .logoutSuccessUrl("/System_manager/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}