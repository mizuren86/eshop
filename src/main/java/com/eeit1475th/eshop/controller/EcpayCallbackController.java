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

	}
}
