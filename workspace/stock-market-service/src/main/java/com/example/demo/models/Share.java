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

    @Transient // This field is not stored in the database
    private double currentPrice;
    
//    @Scheduled(fixedRate = 1000)  
//    public void updateSharePrices() {
//        List<Share> shares = shareRepository.findAll();
//
//        for (Share share : shares) {
//            share.updateCurrentPrice();  
//            shareRepository.save(share);  
//        }
//        
//        System.out.println("Stock prices updated...");
//    }
//}

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

    // Utility method to update price within range
    public void updateCurrentPrice() {
        this.currentPrice = priceMin + Math.random() * (priceMax - priceMin);
    }
}
