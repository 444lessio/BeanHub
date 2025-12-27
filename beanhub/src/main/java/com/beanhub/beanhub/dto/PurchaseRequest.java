package com.beanhub.beanhub.dto;

import java.math.BigDecimal;

public class PurchaseRequest {
    private String drinkName;
    private int sugar;
    private BigDecimal price;

    // Getter e Setter sono obbligatori per il JSON parsing automatico
    public String getDrinkName() { return drinkName; }
    public void setDrinkName(String drinkName) { this.drinkName = drinkName; }

    public int getSugar() { return sugar; }
    public void setSugar(int sugar) { this.sugar = sugar; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}