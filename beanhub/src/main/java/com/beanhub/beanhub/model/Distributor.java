package com.beanhub.beanhub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "distributors")
public class Distributor {

    @Id
    private String id;
    private String location;
    private String status;
    
    private double latitude;
    private double longitude;

    // --- NUOVO CAMPO MANCANTE ---
    private String connectedUser; 
    // ----------------------------

    private int coffeeLevel;
    private int milkLevel;
    private int sugarLevel;
    private int cupsLevel;

    private LocalDateTime lastHeartbeat;

    // Costruttore Vuoto
    public Distributor() {}

    // Costruttore (Usato nel LoadDatabase - NON TOCCARE)
    public Distributor(String id, String location, String status, 
                       double latitude, double longitude, 
                       int coffeeLevel, int milkLevel, int sugarLevel, int cupsLevel) {
        this.id = id;
        this.location = location;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coffeeLevel = coffeeLevel;
        this.milkLevel = milkLevel;
        this.sugarLevel = sugarLevel;
        this.cupsLevel = cupsLevel;
        // connectedUser rimane null all'inizio, è corretto così.
    }

    // --- GETTERS E SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    // --- ECCO I METODI CHE RISOLVONO I TUOI ERRORI ---
    public String getConnectedUser() { return connectedUser; }
    public void setConnectedUser(String connectedUser) { this.connectedUser = connectedUser; }
    // -------------------------------------------------

    public int getCoffeeLevel() { return coffeeLevel; }
    public void setCoffeeLevel(int coffeeLevel) { this.coffeeLevel = coffeeLevel; }

    public int getMilkLevel() { return milkLevel; }
    public void setMilkLevel(int milkLevel) { this.milkLevel = milkLevel; }

    public int getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(int sugarLevel) { this.sugarLevel = sugarLevel; }

    public int getCupsLevel() { return cupsLevel; }
    public void setCupsLevel(int cupsLevel) { this.cupsLevel = cupsLevel; }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
}