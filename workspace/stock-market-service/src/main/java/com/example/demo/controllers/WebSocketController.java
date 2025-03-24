package com.example.demo.controllers;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Share;
import com.example.demo.services.PriceUpdateService;

@RestController
@CrossOrigin(originPatterns = "*")
public class WebSocketController {

    private final PriceUpdateService priceUpdateService;

    public WebSocketController(PriceUpdateService priceUpdateService) {
        this.priceUpdateService = priceUpdateService;
    }

    //Yeh map hoga: /app/getPrices	
    @MessageMapping("/getPrices")  
    @SendTo("/topic/prices")       
    public List<Share> sendInitialPrices() {
        return priceUpdateService.getSharesWithUpdatedPrices();
    }
    
    
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
      Thread.sleep(1000); 
      return message;
    }
}
