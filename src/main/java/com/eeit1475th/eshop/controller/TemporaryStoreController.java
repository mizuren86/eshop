package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.entity.SelectedStore;
import com.eeit1475th.eshop.cart.repository.SelectedStoreRepository;

@RestController
@RequestMapping("/api")
public class TemporaryStoreController {

    @Autowired
    private SelectedStoreRepository selectedStoreRepository;

    @PostMapping("/temporaryStore")
    public ResponseEntity<Integer> createTemporaryStore(@RequestBody SelectedStore store) {
        SelectedStore savedStore = selectedStoreRepository.save(store);
        // 回傳存入資料庫後的主鍵值
        return ResponseEntity.ok(savedStore.getSelectedStoreId());
    }
    
    // 用來從資料庫中取得特定暫存門市記錄
    @GetMapping("/temporaryStore/{id}")
    public ResponseEntity<SelectedStore> getTemporaryStore(@PathVariable Integer id) {
        return selectedStoreRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}