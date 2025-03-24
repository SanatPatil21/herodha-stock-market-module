package com.example.demo.models;


public class PriceRecord {
    private String stockName;
    private double currentPrice;

    public PriceRecord(String stockName, double currentPrice) {
        this.stockName = stockName;
        this.currentPrice = currentPrice;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return "PriceRecord{" +
                "stockName='" + stockName + '\'' +
                ", currentPrice=" + currentPrice +
                '}';
    }
}
