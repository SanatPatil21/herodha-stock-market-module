package com.example.demo.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Candle;
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
    
    
    @MessageMapping("/getCandleData")
    @SendTo("/topic/intraday")
    public Map<Long, LinkedList<Candle>> sendCandleData(Map<Long, LinkedList<Candle>> candleData){
    	return candleData;
    }
   
}
