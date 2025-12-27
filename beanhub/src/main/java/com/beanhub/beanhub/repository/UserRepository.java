package com.beanhub.beanhub.repository;

import com.beanhub.beanhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Nota: JpaRepository<User, String> perché il tuo ID (username) è una Stringa!
public interface UserRepository extends JpaRepository<User, String> {
    
    // Dato che username è l'ID, findById farebbe la stessa cosa, 
    // ma manteniamo questo per leggibilità nel codice.
    Optional<User> findByUsername(String username);
}