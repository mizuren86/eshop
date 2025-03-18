package com.eeit1475th.eshop.cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;

@Service
public class PaymentsService {
	
	// 暫時Url
	@Value("${payment.gateway.url:https://api.mock-payment.com}")
    private String paymentGatewayUrl;
	
	// 暫時Key
	@Value("${payment.gateway.apiKey:mock-api-key}")
	private String apiKey;

	@Autowired
    private OrdersRepository ordersRepository;
	
	@Autowired
    private RestTemplate restTemplate;

    // 發起支付請求
	public String initiatePayment(Integer orderId) {
	    try {
	        Orders order = ordersRepository.findById(orderId)
	                .orElseThrow(() -> new RuntimeException("訂單未找到"));

	        String paymentRequestPayload = buildPaymentRequestPayload(order);
	        String response = restTemplate.postForObject(paymentGatewayUrl + "/pay", paymentRequestPayload, String.class);
	        
	        // 假設支付平台會返回支付網址
	        return response;
	    } catch (Exception e) {
	        // 可以記錄具體的錯誤信息
	        e.printStackTrace();
	        throw new RuntimeException("支付請求發送失敗", e);
	    }
	}

    private String buildPaymentRequestPayload(Orders order) {
        // 這裡根據實際支付接口的要求構建請求的資料
        return "{\"orderId\": \"" + order.getOrderId() + "\", \"amount\": \"" + order.getTotalAmount() + "\"}";
    }

    // 處理支付回調
    public void handlePaymentCallback(String ecPayTradeNo, Integer orderId, String status) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("訂單未找到"));

        if ("success".equalsIgnoreCase(status)) {
            order.setPaymentStatus(PaymentStatus.Paid);
        } else {
            order.setPaymentStatus(PaymentStatus.Failed);
        }

        ordersRepository.save(order);
    }

}
