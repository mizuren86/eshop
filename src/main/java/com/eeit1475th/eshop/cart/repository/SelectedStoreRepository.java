package com.eeit1475th.eshop.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.SelectedStore;

public interface SelectedStoreRepository extends JpaRepository<SelectedStore, Integer> {
}