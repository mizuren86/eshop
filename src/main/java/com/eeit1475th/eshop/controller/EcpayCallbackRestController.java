package com.eeit1475th.eshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.dto.SelectedStore;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/selectedStore")
public class EcpayCallbackRestController {
	
	@GetMapping
	public ResponseEntity<SelectedStore> getSelectedStore(HttpSession session) {
	    SelectedStore selectedStore = (SelectedStore) session.getAttribute("selectedStore");
	    if (selectedStore != null) {
	        return ResponseEntity.ok(selectedStore);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

}
