package com.beanhub.beanhub.Controller; // O 'com.beanhub.beanhub.config'

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. Il Bean che decide QUALE lingua usare
    // Stiamo dicendo a Spring: "Salva la lingua scelta dall'utente nella sua sessione (cookie)".
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH); // Lingua di default se l'utente non ha ancora scelto
        return slr;
    }

    // 2. Il Bean che CAPISCE quando l'utente vuole cambiare lingua
    // Stiamo dicendo a Spring: "Ogni volta che vedi un parametro URL chiamato 'lang',
    // usa quello per cambiare la lingua".
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Il nome del parametro: es. /login?lang=it
        return lci;
    }

    // 3. REGISTRIAMO l'interceptor
    // Stiamo dicendo a Spring: "Ok, ora usa l'interceptor che abbiamo appena creato
    // su ogni richiesta che arriva".
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}