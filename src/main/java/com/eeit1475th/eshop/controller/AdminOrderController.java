package com.eeit1475th.eshop.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eeit1475th.eshop.cart.entity.Orders;
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
                PageRequest.of(page, 10, Sort.by("orderDate").descending())
        );
        model.addAttribute("ordersPage", ordersPage);
        return "/pages/admin/admin-orders"; // 對應到 /templates/admin/admin-orders.html
    }

    // 檢視訂單
    @GetMapping("/view/{orderId}")
    public String viewOrder(@PathVariable("orderId") Integer orderId, Model model) {
        Orders order = ordersService.getOrderById(orderId);
        if (order == null) {
            model.addAttribute("errorMessage", "訂單未找到");
            return "admin/order-error";
        }
        model.addAttribute("order", order);
        return "/pages/admin/order-view";
    }

    // 編輯訂單表單
    @GetMapping("/edit/{orderId}")
    public String editOrderForm(@PathVariable("orderId") Integer orderId, Model model) {
        Orders order = ordersService.getOrderById(orderId);
        if (order == null) {
            model.addAttribute("errorMessage", "訂單未找到");
            return "admin/order-error";
        }
        model.addAttribute("order", order);
        return "/pages/admin/order-edit";
    }

    // 處理訂單編輯提交
    @PostMapping("/edit/{orderId}")
    public String editOrderSubmit(@PathVariable("orderId") Integer orderId, Orders orderData, RedirectAttributes redirectAttributes) {
        Orders order = ordersService.getOrderById(orderId);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "訂單未找到");
            return "redirect:/admin-orders";
        }
        // 更新允許修改的欄位
        order.setPaymentStatus(orderData.getPaymentStatus());
        order.setShippingStatus(orderData.getShippingStatus());
        ordersService.updateOrder(order);
        redirectAttributes.addFlashAttribute("message", "訂單更新成功");
        return "redirect:/admin-orders/view/" + orderId;
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
}
