package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.product.dto.ProductDTO;
import com.eeit1475th.eshop.product.service.ProductService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartRestController {

	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@Autowired
	private ProductService productService;
	
	// shippingMethod 、 paymentMethod 存入 session
	@PostMapping("/session/setShippingAndPayment")
    public ResponseEntity<?> setShippingAndPayment(@RequestBody Map<String, String> params, HttpSession session) {
        String shippingMethod = params.get("shippingMethod");
        String paymentMethod = params.get("paymentMethod");

        if (shippingMethod != null && !shippingMethod.trim().isEmpty()) {
            session.setAttribute("shippingMethod", shippingMethod);
        }
        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
            session.setAttribute("paymentMethod", paymentMethod);
        }
        return ResponseEntity.ok().build();
    }

	// 取得購物車資料
	@GetMapping
	public ResponseEntity<?> getCart(@SessionAttribute(value = "user", required = false) Users user, HttpSession session) {
	    if (user == null) {
	        @SuppressWarnings("unchecked")
	        List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
	        if (tempCart == null) {
	            tempCart = new ArrayList<>();
	        }
	        // 計算小計
	        BigDecimal totalAmount = BigDecimal.ZERO;
	        for (CartItemsDTO item : tempCart) {
	            totalAmount = totalAmount.add(item.getSubTotal());
	        }
	        Map<String, Object> response = new HashMap<>();
	        response.put("cartItems", tempCart);
	        response.put("totalAmount", totalAmount);
	        return ResponseEntity.ok(response);
	    } else {
	        List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(user.getUserId());
	        BigDecimal totalAmount = BigDecimal.ZERO;
	        for (CartItemsDTO item : cartItems) {
	            totalAmount = totalAmount.add(item.getSubTotal());
	        }
	        Map<String, Object> response = new HashMap<>();
	        response.put("cartItems", cartItems);
	        response.put("totalAmount", totalAmount);
	        return ResponseEntity.ok(response);
	    }
	}

	// 加入商品到購物車
	@PostMapping("/add")
	public ResponseEntity<?> addToCart(@RequestParam Integer productId,
			@RequestParam(defaultValue = "1") Integer quantity, HttpSession session,
			@SessionAttribute(value = "user", required = false) Users user) {
		// 如果用戶已登入，就存入資料庫或用戶的購物車資料中
		if (user != null) {
			shoppingCartService.addToCart(user.getUserId(), productId, quantity);
		} else {
			// 未登入狀態，先將購物車數據存放在 session 中
			
			// 取得 ProductDTO
			ProductDTO productDTO = productService.getProductDTOById(productId);
	        if(productDTO == null){
	            return ResponseEntity.badRequest().body("無此商品");
	        }
	        BigDecimal price = productDTO.getUnitPrice();
			
	        @SuppressWarnings("unchecked")
			List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
			if (tempCart == null) {
				tempCart = new ArrayList<>();
			}
			
			// 檢查購物車中是否已存在此商品
	        boolean productExists = false;
	        for (CartItemsDTO item : tempCart) {
	            if (item.getProduct().getProductId().equals(productId)) {
	                // 若存在則更新數量與小計
	                int newQuantity = item.getQuantity() + quantity;
	                item.setQuantity(newQuantity);
	                item.setSubTotal(price.multiply(new BigDecimal(newQuantity)));
	                productExists = true;
	                break;
	            }
	        }
	        
	     // 若購物車中沒有此商品，則新增購物車項目
	        if (!productExists) {
			
			// 取得或初始化計數器
	        Integer counter = (Integer) session.getAttribute("tempCartCounter");
	        if (counter == null) {
	            counter = 1;
	        } else {
	            counter++;
	        }
	        session.setAttribute("tempCartCounter", counter);
	        
	        // 計算小計
	        BigDecimal subTotal = price.multiply(new BigDecimal(quantity));
			
			// 建立 CartItemsDTO 並加入暫存購物車中
			CartItemsDTO newItem = new CartItemsDTO();
			newItem.setCartItemId(counter); 
			newItem.setQuantity(quantity);
			newItem.setPrice(price);
			newItem.setSubTotal(subTotal);
			newItem.setProduct(productDTO);
			tempCart.add(newItem);
	        }
			session.setAttribute("tempCart", tempCart);
		}
		return ResponseEntity.ok("商品已加入購物車");
	}

	// 移除購物車商品
	@PostMapping("/remove")
	public ResponseEntity<?> removeCartItem(@RequestParam("cartItemId") Integer cartItemId,
	        HttpSession session,
	        @SessionAttribute(value = "user", required = false) Users user) {
	    if (user == null) {
	        // 未登入狀態：從 session 中移除該項目
	        @SuppressWarnings("unchecked")
	        List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
	        if (tempCart == null) {
	            return ResponseEntity.badRequest().body("購物車為空");
	        }
	        boolean removed = tempCart.removeIf(item -> 
	            item.getCartItemId() != null && item.getCartItemId().equals(cartItemId)
	        );
	        if (!removed) {
	            return ResponseEntity.badRequest().body("找不到購物車項目");
	        }
	        session.setAttribute("tempCart", tempCart);
	        return ResponseEntity.ok("移除成功");
	    } else {
	        shoppingCartService.removeCartItem(cartItemId);
	        return ResponseEntity.ok("移除成功");
	    }
	}

	// 更新購物車內商品數量
	@PostMapping("/update")
	public ResponseEntity<?> updateCartItem(@RequestParam("cartItemId") Integer cartItemId,
	        @RequestParam("quantity") Integer quantity,
	        HttpSession session,
	        @SessionAttribute(value = "user", required = false) Users user) {
	    if (user == null) {
	        // 未登入狀態：更新 session 中的 tempCart
	        @SuppressWarnings("unchecked")
	        List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
	        if (tempCart == null) {
	            return ResponseEntity.badRequest().body("購物車為空");
	        }
	        boolean found = false;
	        for (CartItemsDTO item : tempCart) {
	            // 注意：這裡假設 CartItemsDTO 的 cartItemId 是可用來辨識該項目的唯一標識，
	            // 如果未登入時沒有自動產生 id，可能需要使用陣列索引或其他方式。
	            if (item.getCartItemId() != null && item.getCartItemId().equals(cartItemId)) {
	                item.setQuantity(quantity);
	                item.setSubTotal(item.getPrice().multiply(new BigDecimal(quantity)));
	                found = true;
	                break;
	            }
	        }
	        if (!found) {
	            return ResponseEntity.badRequest().body("找不到購物車項目");
	        }
	        session.setAttribute("tempCart", tempCart);
	        return ResponseEntity.ok("更新成功");
	    } else {
	        shoppingCartService.updateCartItem(cartItemId, quantity);
	        return ResponseEntity.ok("更新成功");
	    }
	}

	// 清空購物車
	@PostMapping("/clear")
	public ResponseEntity<?> clearCart(@SessionAttribute(value = "user", required = false) Users user) {
		if (user == null) {
			return ResponseEntity.badRequest().body("未登入");
		}
		shoppingCartService.clearCart(user.getUserId());
		return ResponseEntity.ok("清空購物車成功");
	}
}
