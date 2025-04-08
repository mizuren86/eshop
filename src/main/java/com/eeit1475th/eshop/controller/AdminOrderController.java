package com.eeit1475th.eshop.controller;

import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.cart.dto.OrderUpdateDTO;
import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.service.OrdersService;

@Controller
@RequestMapping("/admin-orders") // 修改這裡：直接映射 /admin-orders
public class AdminOrderController {

    @Autowired
    private OrdersService ordersService;

    // 列出所有訂單（依照訂單成立時間由新到舊排序，每頁顯示10筆）
    @GetMapping("")
    public String listOrders(Model model,
                             @org.springframework.web.bind.annotation.RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Orders> ordersPage = ordersService.getAllOrders(
                PageRequest.of(page, 15, Sort.by("orderDate").descending())
        );
        model.addAttribute("ordersPage", ordersPage);
        return "/pages/admin/admin-orders"; // 對應到 /templates/admin/admin-orders.html
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
    public ResponseEntity<?> updateOrderAjax(@PathVariable("orderId") Integer orderId, @RequestBody OrderUpdateDTO updateDto) {
        Orders orders = ordersService.getOrderById(orderId);
        if (orders == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("訂單未找到");
        }
        
        // 更新狀態
        try {
            // 將字串轉換為枚舉（必須確保傳入的字串與枚舉常量名稱完全一致）
            orders.setShippingStatus(ShippingStatus.valueOf(updateDto.getShippingStatus()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無效的狀態值：" + e.getMessage());
        }
        
        ordersService.updateOrder(orders);

        // 回傳更新後的狀態
        Map<String, Object> response = new HashMap<>();
        response.put("shippingStatus", orders.getShippingStatus());
        return ResponseEntity.ok(response);
    }
}
