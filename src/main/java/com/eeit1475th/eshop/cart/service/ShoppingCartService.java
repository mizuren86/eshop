package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.dto.ProductDTO;
import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.cart.repository.CartItemsRepository;
import com.eeit1475th.eshop.cart.repository.ShoppingCartRepository;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductsRepository;

import jakarta.transaction.Transactional;

@Service
public class ShoppingCartService {
	
	@Autowired
	private ShoppingCartRepository shoppingCartRepository;
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private CartItemsRepository cartItemsRepository;
	
	@Autowired
	private UsersRepository userRepository;
	
	// 新增商品到購物車
    @Transactional
    public void addToCart(Integer userId, Integer productId, Integer quantity) {
    	// 找到使用者
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 找到購物車
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUsers(user);
            shoppingCartRepository.save(shoppingCart);
        }

        // 找到商品
        Products product = productsRepository.findById(productId).orElseThrow(() -> new RuntimeException("找不到商品"));

        // 檢查是否已經有相同的商品在購物車
        CartItems existingCartItem = cartItemsRepository.findByShoppingCartAndProducts(shoppingCart, product);
        if (existingCartItem != null) {
            // 商品已存在，更新數量
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartItemsRepository.save(existingCartItem);
        } else {
            // 新增商品到購物車
            CartItems cartItem = new CartItems();
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setProducts(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getUnitPrice());  // 假設價格來自商品
            cartItemsRepository.save(cartItem);
        }
    }

    // 查看購物車內的商品
    public List<CartItemsDTO> getCartItems(Integer userId) {
    	List<CartItemsDTO> cartItemsDTOList = new ArrayList<>();

    	// 查詢該用戶的購物車
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
        if (shoppingCart == null) {
        	return cartItemsDTOList;
        }

        // 根據 ShoppingCart 查詢 CartItems
        List<CartItems> cartItemsList = cartItemsRepository.findByShoppingCart(shoppingCart);

        for (CartItems cartItem : cartItemsList) {
            // 創建購物車明細 DTO
            CartItemsDTO cartItemsDTO = new CartItemsDTO();
            cartItemsDTO.setCartItemId(cartItem.getCartItemId());
            cartItemsDTO.setQuantity(cartItem.getQuantity());
            cartItemsDTO.setPrice(cartItem.getPrice());

            // 計算小計：價格 * 數量
            BigDecimal subTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            cartItemsDTO.setSubTotal(subTotal);
            
            // 創建產品 DTO
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(cartItem.getProducts().getProductId());
            productDTO.setSku(cartItem.getProducts().getSku());
            productDTO.setProductName(cartItem.getProducts().getProductName());
            productDTO.setUnitPrice(cartItem.getProducts().getUnitPrice());
            productDTO.setImageUrl(cartItem.getProducts().getImageUrl());
            cartItemsDTO.setProduct(productDTO);

            // 將 CartItemsDTO 加入列表
            cartItemsDTOList.add(cartItemsDTO);
        }

        return cartItemsDTOList;
    }

    // 更新購物車內商品數量
    @Transactional
    public void updateCartItem(Integer cartItemId, Integer quantity) {
        CartItems cartItem = cartItemsRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("找不到購物車商品"));
        cartItem.setQuantity(quantity);
        cartItemsRepository.save(cartItem);
    }

    // 移除購物車內的商品
    @Transactional
    public void removeCartItem(Integer cartItemId) {
    	CartItems cartItem = cartItemsRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("找不到該商品，cartItemId: " + cartItemId));
    	cartItemsRepository.deleteById(cartItemId);
    }

    // 清空購物車
    @Transactional
    public void clearCart(Integer userId) {
    	ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
        if (shoppingCart != null) {
            cartItemsRepository.deleteByShoppingCart(shoppingCart);
        }
    }

}
