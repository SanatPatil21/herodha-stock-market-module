package com.example.demo.components;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.models.Share;

@Component
public class RabbitMQConsumer {
	
	private final SimpMessagingTemplate messagingTemplate; 

    public RabbitMQConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    private List<Share> sharesCache;
    
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(List<Share> sharesCache) {
    	System.out.println("Recieved Stock Update From Rabbit MQ");
    	System.out.println(LocalTime.now().toString());
    	for (Share share : sharesCache) {
    	    System.out.println(share.toString());
    	}
    	System.out.println("");


        this.sharesCache = sharesCache;  

        messagingTemplate.convertAndSend("/topic/prices", sharesCache);
    }


}
