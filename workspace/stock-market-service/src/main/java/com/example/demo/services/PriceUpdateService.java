package com.example.demo.services;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.models.Share;
import com.example.demo.repositories.ShareRepo;

@Service
public class PriceUpdateService {

	private ShareRepo shareRepository;
	
	private SimpMessagingTemplate messagingTemplate;
	
	private final RabbitTemplate rabbitTemplate;
	
	private List<Share> sharesCache;
	
	public PriceUpdateService(ShareRepo shareRepository, SimpMessagingTemplate messagingTemplate,RabbitTemplate rabbitTemplate) {
        this.shareRepository = shareRepository;
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate=rabbitTemplate;
	}

	public void loadShares() {
	    try {
	        sharesCache = shareRepository.findAll();
	    } catch (Exception e) {
	        System.err.println("Error loading shares from DB: " + e.getMessage());
	        sharesCache = List.of(); 
	    }
	}

	
	//getAllShares from DB
	@jakarta.annotation.PostConstruct
    public void init() {
        loadShares(); 
	}
	
	//Updating Prices 
	@Scheduled(fixedRate = 3000) 
    public void updatePricesAndNotify() {
        if (sharesCache != null) {
            for (Share share : sharesCache) {
                share.updateCurrentPrice(); 
                share.updateCandle();
            }
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "routing.key.#", sharesCache);
        }
    }

    public List<Share> getSharesWithUpdatedPrices() {
        return sharesCache;
    }
    
//    @Scheduled(fixedRate = 60000) 
//    public void resetCandles() {
//        if (sharesCache != null) {
//            for (Share share : sharesCache) {
//                share.initializeCandle(); 
//            }
//            System.out.println("Candle Reset and New Period Started");
//        }
//    }
    
    
    @Scheduled(fixedRate = 60000) 
    public void resetCandlePeriod() {
        if (sharesCache != null) {
            for (Share share : sharesCache) {
                share.setNewCandle(true);
                share.initializeCandle(); 
            }
            System.out.println("Candle Reset and New Period Started");
        }
    }

    
    @Scheduled(fixedRate = 1000) 
    public void updateCandles() {
        if (sharesCache != null) {
            for (Share share : sharesCache) {
                share.updateCandle();
            }
        }
    }


    
    //Getting the Candle Sticks for a Specific Stock
    public Share getCandleSticks(String stockName) {
        for (Share share : sharesCache) {
            if (share.getName().equalsIgnoreCase(stockName)) {
                return share;
            }
        }
        Share emptyShare = new Share(stockName, 0, 0.0, 0.0);
        emptyShare.initializeCandle();
        return emptyShare;
    }

    
    
}
