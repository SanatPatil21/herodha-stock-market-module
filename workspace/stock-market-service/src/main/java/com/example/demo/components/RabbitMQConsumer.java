package com.example.demo.components;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    
    
    
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(List<Share> sharesCache) {
    	System.out.println("Recieved Stock Update From Rabbit MQ");
    	System.out.println(LocalTime.now().toString());
    	for (Share share : sharesCache) {
    	    System.out.println(share.toString());
    	    updatePriceHistory(share);
    	}
    	System.out.println("");

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


}
