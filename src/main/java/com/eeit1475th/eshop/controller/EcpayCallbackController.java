package com.eeit1475th.eshop.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeit1475th.eshop.cart.entity.SelectedStore;
import com.eeit1475th.eshop.cart.repository.SelectedStoreRepository;
import com.eeit1475th.eshop.cart.service.OrdersService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/ecpay")
public class EcpayCallbackController {

	private static final Logger logger = LogManager.getLogger(CheckoutRestController.class);

	@Autowired
	private SelectedStoreRepository selectedStoreRepository;

	@Autowired
	private OrdersService ordersService;

	/**
	 * 處理綠界同步回調，讓使用者返回網站頁面
	 */
	@RequestMapping(value = "/callback", method = { RequestMethod.GET, RequestMethod.POST })
	public String callback(@RequestParam Map<String, String> params, HttpServletRequest request) {
		logger.info("收到綠界同步回調參數: {}", params);

		// 取得綠界回傳的門市資訊
		String CVSStoreID = params.get("CVSStoreID");
		String CVSStoreName = params.get("CVSStoreName");
		String CVSAddress = params.get("CVSAddress");
		String CVSTelephone = params.get("CVSTelephone");

		// 如果有門市資訊，存入資料庫並取得該臨時 SelectedStore 的 ID
		if (CVSStoreID != null && !CVSStoreID.trim().isEmpty()) {
			SelectedStore storeEntity = new SelectedStore(CVSStoreID, CVSStoreName, CVSAddress, CVSTelephone);
			SelectedStore savedStore = selectedStoreRepository.save(storeEntity);
			logger.info("已儲存臨時 SelectedStore，ID：{}", savedStore.getSelectedStoreId());

			// 組合 redirect URL 並附上門市ID參數（例如導向到 /checkout 頁面）
			String shippingMethod = params.getOrDefault("shippingMethod", "711-cod");
			String paymentMethod = params.getOrDefault("paymentMethod", "711-cod-only");
			String redirectUrl = "redirect:http://localhost:8080/checkout?temporaryStoreId="
					+ savedStore.getSelectedStoreId() + "&shippingMethod=" + shippingMethod + "&paymentMethod="
					+ paymentMethod;
			return redirectUrl;
		}
		// 如果沒有門市資訊，直接重導向到首頁或其他頁面
		return "redirect:http://localhost:8080/checkout";

//		// 建立 SelectedStore 實體，並存入資料庫（作為臨時記錄）
//		SelectedStore storeEntity = new SelectedStore(CVSStoreID, CVSStoreName, CVSAddress, CVSTelephone);
//		SelectedStore savedStore = selectedStoreRepository.save(storeEntity);

//		// 從 callback 的參數中嘗試讀取 shippingMethod 和 paymentMethod
//		String shippingMethod = params.get("shippingMethod");
//		// 如果 callback 沒有傳，就嘗試從 session 中取得
//		if (shippingMethod == null || shippingMethod.trim().isEmpty()) {
//			shippingMethod = (String) request.getSession().getAttribute("shippingMethod");
//			if (shippingMethod == null || shippingMethod.trim().isEmpty()) {
//				shippingMethod = "711-cod"; // 預設值
//			}
//		}
//
//		String paymentMethod = params.get("paymentMethod");
//		if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
//			paymentMethod = (String) request.getSession().getAttribute("paymentMethod");
//			if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
//				paymentMethod = "711-cod-only"; // 預設值
//			}
//		}

//		// 驗證回傳參數是否正確（例如檢查交易狀態、檢查檢查碼等）
//		boolean isPaymentSuccessful = verifyPayment(params);
//		if (isPaymentSuccessful) {
//			// 從回傳參數或 session 中取得訂單編號（例如 orderId）
//			String orderIdStr = params.get("orderId");
//			if (orderIdStr != null) {
//				try {
//					Integer orderId = Integer.valueOf(orderIdStr);
//					ordersService.updateOrderStatus(orderId, "已付款");
//					logger.info("訂單 {} 狀態更新為已付款", orderId);
//				} catch (NumberFormatException e) {
//					logger.error("訂單編號格式錯誤：{}", orderIdStr, e);
//				}
//			} else {
//				logger.warn("回傳參數中未包含 orderId，無法更新訂單狀態");
//			}
//		} else {
//			logger.warn("付款驗證失敗，未更新訂單狀態");
//		}
//
//		// 組合 redirect URL，附上剛剛存入的臨時 SelectedStore 的 ID
//		String redirectUrl = "redirect:http://localhost:8080/checkout?temporaryStoreId="
//				+ savedStore.getSelectedStoreId() + "&shippingMethod=" + shippingMethod + "&paymentMethod="
//				+ paymentMethod;
//		return redirectUrl;
	}

//	private boolean verifyPayment(Map<String, String> params) {
//		String paymentResult = params.get("RtnCode");
//		return "1".equals(paymentResult); // 1 代表成功
//	}
}
