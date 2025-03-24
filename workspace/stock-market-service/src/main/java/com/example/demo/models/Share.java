package com.example.demo.models;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Random;

@Entity
@Table(name = "shares")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("stockId") 
    private Long id;

    private String name;

    private int quantity;

    private double priceMin;

    private double priceMax;

    @Transient 
    private double currentPrice;
    

    // Constructors
    public Share() {
    }

    public Share(String name, int quantity, double priceMin, double priceMax) {
        this.name = name;
        this.quantity = quantity;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.currentPrice = priceMin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(double priceMin) {
        this.priceMin = priceMin;
    }

    public double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public double getCurrentPrice() {
    	this.updateCurrentPrice();
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    @Override
    public String toString() {
        return "Share{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", currentPrice=" + currentPrice +
                '}';
    }

    
    

    
  

    public void updateCurrentPrice() {
        Random random = new Random();
        double fluctuationPercentage;

       
        if (random.nextDouble() < 0.05) {  
            fluctuationPercentage = 0.10 + random.nextDouble() * 0.05; 
        } else {
        	
            fluctuationPercentage = 0.003 + random.nextDouble() * 0.012; 
        }

        double change = currentPrice * fluctuationPercentage;

        // 70% chance that joh hai wahi continue hoga
        if (random.nextDouble() < 0.7) {
            // Continue previous trend
            if (random.nextBoolean()) {
                this.currentPrice = Math.min(priceMax, currentPrice + change); 
            } else {
                this.currentPrice = Math.max(priceMin, currentPrice - change);
            }
        } else {
            //30% chance ulta hoga
            if (random.nextBoolean()) {
                this.currentPrice = Math.max(priceMin, currentPrice - change); 
            } else {
                this.currentPrice = Math.min(priceMax, currentPrice + change);
            }
        }

       
        this.currentPrice = Math.max(priceMin, Math.min(priceMax, this.currentPrice));

        //Sudden Dips or rise
        if (random.nextDouble() < 0.01) {
            double correctionFactor = 0.05 + random.nextDouble() * 0.10; 
            if (random.nextBoolean()) {
                this.currentPrice = Math.min(priceMax, currentPrice * (1 + correctionFactor));
            } else {
                this.currentPrice = Math.max(priceMin, currentPrice * (1 - correctionFactor));
            }
        }
    }

}