package com.beanhub.beanhub.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "drinks")
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(precision = 10, scale = 2) // Opzionale ma consigliato per i soldi
    private BigDecimal price;

    // Ingredienti necessari (es. quanti grammi/unità consuma)
    private Integer coffee; 
    private Integer milk;
    private Integer sugar;

    // 1. COSTRUTTORE VUOTO (OBBLIGATORIO PER JPA)
    public Drink() {
    }

    // 2. COSTRUTTORE CHE USI IN LoadDatabase (CORRETTO)
    public Drink(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        // Inizializziamo a 0 gli ingredienti per evitare NullPointerException
        this.coffee = 0;
        this.milk = 0;
        this.sugar = 0;
    }

    // 3. COSTRUTTORE COMPLETO
    public Drink(String name, BigDecimal price, Integer coffee, Integer milk, Integer sugar) {
        this.name = name;
        this.price = price;
        this.coffee = coffee;
        this.milk = milk;
        this.sugar = sugar;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getCoffee() { return coffee; }
    public Integer getMilk() { return milk; }
    public Integer getSugar() { return sugar; }

    // SETTERS (AGGIUNTI - IMPORTANTI)
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCoffee(Integer coffee) { this.coffee = coffee; }
    public void setMilk(Integer milk) { this.milk = milk; }
    public void setSugar(Integer sugar) { this.sugar = sugar; }
}