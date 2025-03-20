package com.example.demo.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.models.Share;
import com.example.demo.repositories.ShareRepo;



@Service
public class PriceUpdateScheduler {

    private final ShareRepo shareRepository;

    public PriceUpdateScheduler(ShareRepo shareRepository) {
        this.shareRepository = shareRepository;
    }

    @Scheduled(fixedRate = 1000)  
    public void updateSharePrices() {
        List<Share> shares = shareRepository.findAll();

        for (Share share : shares) {
            share.updateCurrentPrice();  
            shareRepository.save(share);  
        }
        
        System.out.println("Stock prices updated...");
    }
}