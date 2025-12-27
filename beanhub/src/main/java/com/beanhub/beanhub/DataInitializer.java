package com.beanhub.beanhub;

import com.beanhub.beanhub.service.UserService;
import com.beanhub.beanhub.repository.UserRepository;
import com.beanhub.beanhub.model.User;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- INIZIALIZZAZIONE DATI (Check esistenza utenti) ---");
        
        // Usiamo un metodo helper per evitare di ripetere il codice
        createUserIfNotFound("mario", "password123", "USER", new BigDecimal("5.00"));
        createUserIfNotFound("luigi", "password123", "USER", new BigDecimal("12.50"));
        
        // QUESTO È IL PUNTO CRUCIALE:
        // Se "admin" esiste già nel DB (con la password cambiata da te), 
        // questo metodo NON farà nulla e la tua password resterà salva.
        createUserIfNotFound("admin", "adminpass", "ADMIN", new BigDecimal("100.00"));

        System.out.println("--- INIZIALIZZAZIONE COMPLETATA ---");
    }

    private void createUserIfNotFound(String username, String password, String role, BigDecimal credit) {
        // 1. Chiediamo al repository: "C'è un utente con questo username?"
        Optional<User> existingUser = userRepository.findByUsername(username);

        // 2. Controllo: Se l'Optional è VUOTO (isEmpty), allora creiamo l'utente.
        // Se è PIENO (isPresent), non facciamo nulla.
        if (existingUser.isEmpty()) {
            userService.createUser(username, password, role, credit);
            System.out.println("✅ Utente creato: " + username + " (Password default)");
        } else {
            System.out.println("ℹ️ Utente già esistente: " + username + " -> SKIP (Password invariata)");
        }
    }
}