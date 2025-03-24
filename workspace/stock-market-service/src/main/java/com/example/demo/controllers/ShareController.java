package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.PurchaseBody;
import com.example.demo.models.Share;
import com.example.demo.services.ShareService;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "*")
public class ShareController {
	
	@Autowired
	private ShareService shareService;
	
	@PostMapping("/add")
	public Share addShare(@RequestBody Share share) {
		return shareService.addShare(share);	
	}
	
	@GetMapping("/all")
	public List<Share> getAllShares(){
		return shareService.getAllShares();
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteShare(@PathVariable Long id) {
		shareService.deleteShareById(id);
		
	}
	
	@PutMapping("/update")
	public ResponseEntity<?> updateShare(@RequestBody Share share) {
	    Share updatedShare = shareService.updateShare(share);
	    if (updatedShare != null) {
	        return ResponseEntity.ok(updatedShare);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Share not found with ID: " + share.getId());
	    }
	}
	
	@PutMapping("/purchase")
	public ResponseEntity<?> updateShareQuantity(@RequestBody PurchaseBody purchaseBody){
		Long stockId = purchaseBody.getStockId();
        int purchaseQuantity = purchaseBody.getPurchaseQuantity();
        
        Optional<Share> share = shareService.getShareById(stockId);
        
        if(share.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.OK).body("Share not found with ID: " + stockId);
        }
        
        if(share.get().getQuantity()<purchaseQuantity) {
        	return ResponseEntity.status(HttpStatus.OK).body("Not enough Shares of Stock ID: " + stockId);
        }
        
        share.get().setQuantity(share.get().getQuantity()-purchaseQuantity);
        	
        System.out.println(share.get());
        
        return ResponseEntity.ok(shareService.updateShare(share.get()));
        
	}
	
	
	@PutMapping("/sell")
	public ResponseEntity<?> increaseShareQuantity(@RequestBody PurchaseBody purchaseBody){
		Long stockId = purchaseBody.getStockId();
        int purchaseQuantity = purchaseBody.getPurchaseQuantity();
        
        Optional<Share> share = shareService.getShareById(stockId);
        
        if(share.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.OK).body("Share not found with ID: " + stockId);
        }
        
        
        share.get().setQuantity(share.get().getQuantity()+purchaseQuantity);
        
        System.out.println(share.get());
        
        return ResponseEntity.ok(shareService.updateShare(share.get()));
        
	}
	
	
	
	
	


}
