package com.eeit1475th.eshop.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.SelectedStore;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class OrderSelectedStoreController {

    @Autowired
    private OrdersRepository ordersRepository;

    @GetMapping("/orderSelectedStore")
    public ResponseEntity<SelectedStore> getOrderSelectedStore(HttpSession session) {
        Integer orderId = (Integer) session.getAttribute("currentOrderId");
        if (orderId == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Orders> optOrder = ordersRepository.findById(orderId);
        if (optOrder.isPresent() && optOrder.get().getSelectedStore() != null) {
            return ResponseEntity.ok(optOrder.get().getSelectedStore());
        }
        return ResponseEntity.notFound().build();
    }
}
