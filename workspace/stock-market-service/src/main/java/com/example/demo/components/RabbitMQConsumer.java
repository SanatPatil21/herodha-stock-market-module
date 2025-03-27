package com.example.demo.components;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.models.Candle;
import com.example.demo.models.Share;

@Component
public class RabbitMQConsumer {
	
	private final SimpMessagingTemplate messagingTemplate; 

    public RabbitMQConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    private List<Share> sharesCache;
    
    private transient Map<String, LinkedList<Map<String, Object>>> priceHistoryCache = new HashMap<>();
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private transient Map<Long,LinkedList<Candle>> candleData = new HashMap<>();
    
    
    
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(List<Share> sharesCache) {
//    	System.out.println("Recieved Stock Update From Rabbit MQ");
//    	System.out.println(LocalTime.now().toString());
    	for (Share share : sharesCache) {
//    	    System.out.println(share.toString());
    	    updatePriceHistory(share);
    	}
//    	System.out.println("");

        this.sharesCache = sharesCache;  
        
        Map<String, Object> response = new HashMap<>();
        response.put("latestPrices", sharesCache);
        response.put("priceHistory", priceHistoryCache);

        messagingTemplate.convertAndSend("/topic/prices", response);
    }
    
    private void updatePriceHistory(Share share) {
        String stockName = share.getName(); 

        Map<String, Object> priceEntry = new HashMap<>();
        priceEntry.put("stockName", stockName);
        priceEntry.put("price", share.getCurrentPrice());
        priceEntry.put("timestamp",LocalTime.now().format(formatter));

        LinkedList<Map<String, Object>> priceList = priceHistoryCache.computeIfAbsent(stockName, k -> new LinkedList<>());
        priceList.add(priceEntry);

        if (priceList.size() > 20) {
            priceList.removeFirst();
        }

        priceHistoryCache.put(stockName, priceList);
    }

    
    //Listener for candle Data
    @RabbitListener(queues = RabbitMQConfig.SECOND_QUEUE_NAME)
    public void listenToSecondQueue(List<Share> sharesCache) {
    	for(Share share:sharesCache) {
    		updateCandleData(share);
    	}
//    	System.out.println("");
    	
    	messagingTemplate.convertAndSend("/topic/intraday", candleData);
    }
    
    private void updateCandleData(Share share) {
    	Long stockId = share.getId();    	
    	
    	LinkedList<Candle> candles = candleData.computeIfAbsent(stockId, k -> new LinkedList<>());
    	
    	if(candles.isEmpty()) {
    		Candle newCandle = createNewCandle(share.getCurrentPrice());
            candles.add(newCandle);
    	}else {
    		Candle lastCandle = candles.getLast();
    		
    		if(isNewCandleRequired(lastCandle)) {
    			System.out.println(LocalTime.now());
    			System.out.println("New Candle Created");
    			Candle newCandle = createNewCandle(share.getCurrentPrice());
                candles.add(newCandle);
                 
                 if (candles.size() > 50) {
                     candles.removeFirst(); 	
                 }
    		}else {
    			updateExistingCandle(lastCandle,share.getCurrentPrice());
    			
    		}
    	}
    	
    	candleData.put(stockId, candles);
    	
    	
    }
    
    
    private Candle createNewCandle(double price) {
        Candle candle = new Candle();
        candle.setOpenPrice(price);
        candle.setClosePrice(price);
        candle.setMinPrice(price);
        candle.setMaxPrice(price);
        candle.setTimestamp(LocalDateTime.now());
        return candle;
    }
    
    private void updateExistingCandle(Candle candle,double price) {
    	candle.setClosePrice(price);
    	candle.setMinPrice(Math.min(candle.getMinPrice(), price));
        candle.setMaxPrice(Math.max(candle.getMaxPrice(), price));
     // candle.setTimestamp(LocalDateTime.now());
    }
    
    private boolean isNewCandleRequired(Candle lastCandle) {
    	//New candle every 30 seconds
    	long minutesElapsed = ChronoUnit.SECONDS.between(lastCandle.getTimestamp(), LocalDateTime.now());
        return minutesElapsed >= 20;
    }
    
    public Map<Long, LinkedList<Candle>> getCandleData() {
        return candleData;
    }
    
    

}
