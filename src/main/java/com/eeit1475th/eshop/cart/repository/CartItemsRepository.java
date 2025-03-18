package com.eeit1475th.eshop.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.product.entity.Products;

public interface CartItemsRepository extends JpaRepository<CartItems, Integer> {
	
	// 根據 ShoppingCart 查詢 CartItems
    List<CartItems> findByShoppingCart(ShoppingCart shoppingCart);

    // 根據購物車和商品查詢該商品是否在購物車中
    CartItems findByShoppingCartAndProducts(ShoppingCart shoppingCart, Products products);

    // 刪除指定商品
    void deleteByShoppingCartAndProducts(ShoppingCart shoppingCart, Products products);

    // 刪除購物車中的所有商品
    void deleteByShoppingCart(ShoppingCart shoppingCart);

}
