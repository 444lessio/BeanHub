package com.beanhub.beanhub.config;

import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal; // Importante se hai il campo credit

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // Controlliamo se l'admin esiste già
            if (!userRepository.existsById("admin")) {
                
                // --- MODIFICA QUI: Usiamo il costruttore vuoto e i SETTER ---
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin");
                admin.setRole("ADMIN");
                
                // Se la tua classe User ha il campo 'credit', settalo per evitare errori null
                try {
                     admin.setCredit(BigDecimal.ZERO); 
                } catch (Exception e) {
                    // Se non hai il campo credit, ignora questo blocco
                }

                userRepository.save(admin);
                System.out.println("✅ Utente ADMIN creato: user=admin, pass=admin");
            }
        };
    }
}