package com.eeit1475th.eshop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.service.OrdersService;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import ecpay.payment.integration.ecpayOperator.EcpayFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutRestController {

	private static final Logger logger = LogManager.getLogger(CheckoutRestController.class);

	// 測試環境參數
	private static final String MERCHANT_ID = "3002607";
	private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
	private static final String HASH_IV = "EkRm7iFT261dpevs";

	private AllInOne allInOne;

	@Autowired
	private OrdersService ordersService;

	// 建構子中初始化綠界 SDK 參數
	public CheckoutRestController() {
		allInOne = new AllInOne("payment_conf.xml");
		logger.info("CheckoutRestController initialized");
	}

	// selectedCity 、 selectedDistrict 存入 session
	@PostMapping("/session/setShippingAddress")
	public Map<String, String> setShippingAddress(@RequestParam("selectedCity") String selectedCity,
			@RequestParam("selectedDistrict") String selectedDistrict, HttpSession session) {
		session.setAttribute("selectedCity", selectedCity);
		session.setAttribute("selectedDistrict", selectedDistrict);
		return Map.of("selectedCity", selectedCity, "selectedDistrict", selectedDistrict);
	}

	/**
	 * 提交訂單，產生綠界付款頁面自動送出表單 前端呼叫此 API 時，必須以 POST 方式傳入訂單編號（orderId） 依運送與付款方式分流處理
	 */
	@PostMapping("/submitOrder")
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpSession session = request.getSession();

			// Step 0-1: 讀取運送方式參數
			String shippingMethod = request.getParameter("shippingMethod");
			if (shippingMethod == null || shippingMethod.trim().isEmpty()
					|| "null".equalsIgnoreCase(shippingMethod.trim())) {
				throw new RuntimeException("缺少運送方式參數");
			}
			session.setAttribute("shippingMethod", shippingMethod);
			logger.info("收到運送方式參數：{}", shippingMethod);

			// Step 0-2: 讀取付款方式參數
			String paymentMethod = request.getParameter("paymentMethod");
			if (paymentMethod == null || paymentMethod.trim().isEmpty()
					|| "null".equalsIgnoreCase(paymentMethod.trim())) {
				paymentMethod = (String) session.getAttribute("paymentMethod");
				if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
					throw new RuntimeException("缺少付款方式參數");
				}
			}
			session.setAttribute("paymentMethod", paymentMethod);
			logger.info("付款方式參數：{}", paymentMethod);

			// Step 0-3: 讀取訂單備註，這個欄位只存入資料庫，不會傳給綠界
			String orderRemark = request.getParameter("orderRemark");
			logger.info("收到訂單備註：{}", orderRemark);

			// Step 1: 取得或建立訂單（從購物車建立訂單）
			String orderIdStr = request.getParameter("orderId");
			Orders order;
			if (orderIdStr == null || orderIdStr.isEmpty()) {
				// 如果 orderId 不存在，則從購物車建立訂單
				order = ordersService.createOrderFromCart(request.getSession(), request);
			} else {
				// 如果已經有 orderId，則透過 Service 取得訂單詳情（Service 封裝了對 Repository 的調用）
				order = ordersService.getOrderById(Integer.valueOf(orderIdStr));
				if (order == null) {
					logger.error("Order not found: " + orderIdStr);
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
					return;
				}
			}

			// 訂單備註存入訂單
			order.setOrderRemark(orderRemark);

//			// 讀取 session 中的優惠券並存入訂單（新增這段程式碼）
//			String couponDiscountStr = (String) session.getAttribute("couponDiscount");
//			if (couponDiscountStr != null && !couponDiscountStr.isEmpty()) {
//			    try {
//			        BigDecimal couponDiscountValue = new BigDecimal(couponDiscountStr);
//			        order.setCouponDiscount(couponDiscountValue);
//			    } catch (NumberFormatException e) {
//			        logger.error("無法轉換優惠券折扣字串: {}", couponDiscountStr, e);
//			    }
//			}

			// Step 2: 根據運送方式分流處理
			if ("711-cod".equalsIgnoreCase(shippingMethod)) {
				// 產生廠商交易編號
				String orderIdPart = order.getOrderId().toString();
				int remainingLength = 20 - orderIdPart.length();
				String randomPart = generateRandomNumericString(remainingLength);
				String merchantTradeNo = orderIdPart + randomPart;
				if (merchantTradeNo.length() > 20) {
					merchantTradeNo = merchantTradeNo.substring(0, 20);
				}
				// 將廠商交易編號設定到訂單中
				order.setMerchantTradeNo(merchantTradeNo);
				ordersService.updateOrder(order);

				logger.info("採用 7-11取貨付款，訂單編號：{}, 交易編號：{}", order.getOrderId(), merchantTradeNo);

				// 直接完成訂單，導向訂單頁面
				response.sendRedirect("/orders");
			} else {
				// 非 7-11取貨付款：處理金流付款
				// 此處直接使用先前存入 Session 中的付款方式
				if ("711-no-cod".equalsIgnoreCase(shippingMethod) && "711-cod-only".equalsIgnoreCase(paymentMethod)) {
					paymentMethod = "credit";
				}

				if ("credit".equalsIgnoreCase(paymentMethod)) {
					// 信用卡付款流程

					// 產生廠商交易編號
					String orderIdPart = order.getOrderId().toString();
					int remainingLength = 20 - orderIdPart.length();
					String randomPart = generateRandomNumericString(remainingLength);
					String merchantTradeNo = orderIdPart + randomPart;
					if (merchantTradeNo.length() > 20) {
						merchantTradeNo = merchantTradeNo.substring(0, 20);
					}

					// 產生交易日期
					String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
					// 取得交易金額（整數）
					String totalAmount = order.getTotalAmount().setScale(0, RoundingMode.DOWN).toPlainString();
					// 設定交易描述與商品名稱
					String tradeDesc = "Fruitables訂單";
					String itemName = generateItemNameFromOrder(order);

					// 建立信用卡一次付清付款請求物件 AioCheckOutOneTime
					AioCheckOutOneTime obj = new AioCheckOutOneTime();
//					obj.setMerchantID(MERCHANT_ID);
					obj.setMerchantTradeNo(merchantTradeNo);
					obj.setMerchantTradeDate(merchantTradeDate);
//					obj.setPaymentType("aio");
					obj.setTotalAmount(totalAmount);
					obj.setTradeDesc(tradeDesc);
					obj.setItemName(itemName);
					obj.setReturnURL("https://dabf-59-125-142-166.ngrok-free.app/ecpay/return");
//					obj.setChoosePayment("Credit");
//					obj.setEncryptType("1");
					obj.setClientBackURL("http://localhost:8080/orders");
					obj.setBidingCard("0");

					// 計算檢查碼 CheckMacValue，利用 SDK 的 EcpayFunction
					String checkMacValue = EcpayFunction.genCheckMacValue(HASH_KEY, HASH_IV, obj);
					logger.info("產生的 CheckMacValue: {}", checkMacValue);

					// 更新訂單中的廠商交易編號
					order.setMerchantTradeNo(merchantTradeNo);
					ordersService.updateOrder(order);

					// 呼叫綠界 SDK 產生付款表單並送出
					String formHtml = allInOne.aioCheckOut(obj, null);
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().write(formHtml);
					logger.info("信用卡付款流程啟動, 訂單編號: {}, 交易編號: {}", order.getOrderId(), merchantTradeNo);
				} else if ("linepay".equalsIgnoreCase(paymentMethod)) {
					// LinePay 付款流程（示意，尚未整合）
					logger.info("LinePay付款流程（尚未實作）, 訂單編號: {}", order.getOrderId());
					response.sendRedirect("/linepay?orderId=" + order.getOrderId());
				} else {
					throw new RuntimeException("不支援的付款方式：" + paymentMethod);
				}
			}
			logger.info("訂單提交成功, 訂單編號: {}", order.getOrderId());
		} catch (Exception e) {
			logger.error("建立訂單或付款表單時發生錯誤", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "建立訂單或付款表單失敗");
		}
	}

	/**
	 * 產生指定長度的隨機數字字串
	 */
	private String generateRandomNumericString(int length) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(rand.nextInt(10)); // 0~9 的隨機數字
		}
		return sb.toString();
	}

	/**
	 * 根據訂單資料組合商品明細字串，例如 "商品A x1#商品B x2" 請根據你自己的訂單物件結構實作此方法
	 */
	private String generateItemNameFromOrder(Orders order) {
		if (order == null) {
			System.out.println("Order is null");
			return "";
		}
		// 若 orderItems 為 lazy loading，可以在這裡強制載入
		order.getOrderItems().size();

		if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
			System.out.println("Order " + order.getOrderId() + " has no order items");
			return "無商品";
		}

		String itemNames = order.getOrderItems().stream().filter(item -> {
			if (item.getProducts() == null) {
				System.out.println("OrderItem " + item.getOrderItemId() + " has no product");
				return false;
			}
			if (item.getProducts().getProductName() == null || item.getProducts().getProductName().trim().isEmpty()) {
				System.out.println("OrderItem " + item.getOrderItemId() + " has empty product name");
				return false;
			}
			return true;
		}).map(item -> {
			String productName = item.getProducts().getProductName();
			int quantity = item.getQuantity();
			System.out.println("OrderItem " + item.getOrderItemId() + ": " + productName + " x" + quantity);
			return productName + " x" + quantity;
		}).collect(Collectors.joining("#"));

		if (itemNames.isEmpty()) {
			itemNames = "無商品";
		}

		return itemNames;
	}

}
