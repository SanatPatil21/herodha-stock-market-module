package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Share;
import com.example.demo.repositories.ShareRepo;

@Service
public class ShareService {

	@Autowired
	private ShareRepo shareRepo;
	
	public Share addShare(Share share) {
		return shareRepo.save(share);
		 
	}
	
	public List<Share> getAllShares(){
		return shareRepo.findAll();
	}
	
	public void deleteShareById(Long id) {
		shareRepo.deleteById(id);
	}
	
	
	public Share updateShare(Share share) {
	    if (shareRepo.existsById(share.getId())) {
	        return shareRepo.save(share); 
	    } else {
	        return null; 
	    }
	}
	
	public Optional<Share> getShareById(Long id) {
		return shareRepo.findById(id);
	}

}
