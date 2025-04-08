package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.cart.repository.CartItemsRepository;
import com.eeit1475th.eshop.cart.repository.ShoppingCartRepository;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.product.dto.ProductDTO;
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
        if (!cartItemsRepository.existsById(cartItemId)) {
            throw new RuntimeException("找不到該商品，cartItemId: " + cartItemId);
        }
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
    
    /**
     * 合併未登入狀態下暫存的購物車 (tempCart) 到會員購物車中。
     * 如果會員購物車已存在相同產品，則累加數量；若不存在則新增購物車項目。
     *
     * @param userId   會員編號
     * @param tempCart 暫存購物車項目列表 (CartItemsDTO)
     */
    public void mergeTempCartToUserCart(Integer userId, List<CartItemsDTO> tempCart) {
        // 取得會員的購物車，假設購物車與使用者一對一
        ShoppingCart userCart = shoppingCartRepository.findByUsers_UserId(userId);
        if (userCart == null) {
            // 如果會員購物車不存在，根據需求可以建立一個新的購物車
            userCart = new ShoppingCart();
            // 假設有一個 Users 物件可以設定，這邊略過設定過程
            // userCart.setUsers(...);
        }

        // 遍歷暫存購物車項目
        for (CartItemsDTO tempItem : tempCart) {
            // 假設 CartItemsDTO 提供 getProductId() 與 getQuantity()
            Integer productId = tempItem.getProduct().getProductId();
            int tempQuantity = tempItem.getQuantity();

            // 嘗試在會員購物車中尋找相同的商品
            Optional<CartItems> existingItemOpt = 
                userCart.getCartItems().stream()
                .filter(item -> item.getProducts().getProductId().equals(productId))
                .findFirst();

            if (existingItemOpt.isPresent()) {
                // 如果已存在，更新數量（累加）
                CartItems existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + tempQuantity);
            } else {
                // 如果不存在，建立新的 CartItems 實體
                CartItems newItem = new CartItems();
                // 依照實際情況，從資料庫取得產品資訊
                newItem.setProducts(productsRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found, productId=" + productId)));
                newItem.setQuantity(tempQuantity);
                newItem.setPrice(tempItem.getPrice()); // 假設價格從 DTO 取得

                // 將新的購物車項目加入會員購物車
                userCart.getCartItems().add(newItem);
            }
        }

        // 儲存更新後的購物車
        shoppingCartRepository.save(userCart);
    }

}
