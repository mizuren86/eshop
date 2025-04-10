package com.eeit1475th.eshop.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.cart.dto.OrderUpdateDTO;
import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.service.OrdersService;

@Controller
@RequestMapping("/admin-orders")
public class AdminOrderController {

	@Autowired
	private OrdersService ordersService;

	// 列出所有訂單（依照訂單成立時間由新到舊排序，每頁顯示15筆）
	@GetMapping("")
	public String listOrders(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "merchantTradeNo", required = false) String merchantTradeNo,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "paymentStatus", required = false) String paymentStatus,
			@RequestParam(value = "shippingStatus", required = false) String shippingStatus) {

		// 呼叫 service 中的搜尋方法（模糊查詢）
		Page<Orders> ordersPage = ordersService.searchOrders(merchantTradeNo, orderId, paymentStatus, shippingStatus,
				PageRequest.of(page, 15, Sort.by("orderDate").descending()));

		model.addAttribute("ordersPage", ordersPage);
		model.addAttribute("merchantTradeNo", merchantTradeNo);
		model.addAttribute("orderId", orderId);
		model.addAttribute("paymentStatus", paymentStatus);
		model.addAttribute("shippingStatus", shippingStatus);

		return "/pages/admin/admin-orders";
	}

	// 刪除訂單 API (使用 DELETE 方法)
	@DeleteMapping("/delete/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable("orderId") Integer orderId) {
		try {
			ordersService.deleteOrder(orderId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除訂單失敗");
		}
	}

	@PostMapping("/updateAjax/{orderId}")
	@ResponseBody
	public ResponseEntity<?> updateOrderAjax(@PathVariable("orderId") Integer orderId,
			@RequestBody OrderUpdateDTO updateDto) {
		// 取得訂單
		Orders orders = ordersService.getOrderById(orderId);
		if (orders == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("訂單未找到");
		}

		// 更新配送狀態
		try {
			// 將字串轉換為枚舉（必須確保傳入的字串與枚舉常量名稱完全一致）
			orders.setShippingStatus(ShippingStatus.valueOf(updateDto.getShippingStatus()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無效的狀態值：" + e.getMessage());
		}

		// 呼叫 service 方法更新訂單中的訂單商品
		ordersService.updateOrderItems(orders, updateDto);

		ordersService.updateOrder(orders);

		// 回傳更新後的資料：配送狀態與訂單商品簡要資訊
		Map<String, Object> response = new HashMap<>();
		response.put("shippingStatus", orders.getShippingStatus());
		response.put("orderItems", orders.getOrderItems().stream().map(oi -> {
			Map<String, Object> map = new HashMap<>();
			map.put("productId", oi.getProducts().getProductId());
			map.put("quantity", oi.getQuantity());
			return map;
		}).collect(Collectors.toList()));

		return ResponseEntity.ok(response);
	}
}
