package com.beanhub.beanhub.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "users")
public class User {

    @Id 
    private String username;
    
    private String password;
    private String role; 
    private BigDecimal credit; 

    
    public User() {
    }



    public User(String username, String password, String role, BigDecimal credit) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.credit = credit;
    }

    // --- GETTERS E SETTERS ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }
}