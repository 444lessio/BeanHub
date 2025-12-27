package com.beanhub.beanhub.service;

import com.beanhub.beanhub.model.User;
import com.beanhub.beanhub.repository.UserRepository;

import java.math.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    // Aggiornato per gestire il ROLE
    public void createUser(String username, String password, String role, BigDecimal initialCredit) {
        // Controlliamo se esiste già (usando l'ID che è lo username)
        if (!userRepository.existsById(username)) {
            User newUser = new User(username, password, role, initialCredit);
            userRepository.save(newUser);
        }
    }
}