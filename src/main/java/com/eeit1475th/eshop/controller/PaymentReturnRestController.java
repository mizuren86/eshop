package com.eeit1475th.eshop.controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.service.OrdersService;
import com.eeit1475th.eshop.cart.util.EcpayUtil;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.ecpayOperator.EcpayFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/ecpay")
public class PaymentReturnRestController {

	private static final Logger logger = LogManager.getLogger(PaymentReturnRestController.class);

	private AllInOne allInOne;

	@Autowired
	private OrdersService ordersService;

	public PaymentReturnRestController() {
		String configPath = "payment_conf.xml";
	    allInOne = new AllInOne(configPath);
//		allInOne = new AllInOne("");
		System.out.println("PaymentReturnRestController initialized");
	}

	/**
	 * 處理綠界金流非同步通知（付款結果）
	 */
	@PostMapping("/return")
	public void ecpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain;charset=UTF-8");
		try {
//			StringBuilder rawData = new StringBuilder();
//			try (BufferedReader reader = request.getReader()) {
//				String line;
//				while ((line = reader.readLine()) != null) {
//					rawData.append(line);
//				}
//			}
//			String rawBody = rawData.toString();
//			logger.info("Raw request body: {}", rawBody);
//			
//			String receivedCheckMacValue = request.getParameter("CheckMacValue");
//			logger.info("Received CheckMacValue from request.getParameter: {}", receivedCheckMacValue);


//			// 列印所有請求參數
//			request.getParameterMap().forEach((key, value) -> {
//				logger.info("收到參數 - {}: {}", key, String.join(",", value));
//			});
			
			


//	        String receivedCheckMacValue = request.getParameter("CheckMacValue");
//	        logger.info("Received CheckMacValue from request.getParameter: {}", receivedCheckMacValue);

			 // 印出所有參數以便除錯
	        request.getParameterMap().forEach((key, value) -> {
	            logger.info("收到參數 - {}: {}", key, String.join(",", value));
	        });

//	        // 合併 Query String 與 POST Body 的參數
//	        Map<String, String> allParams = EcpayUtil.mergeRequestParameters(request);
//	        // 重建原始字串
//	        String rawString = EcpayUtil.buildRawStringForCheckMac(allParams);
//	        logger.info("重建的原始字串: {}", rawString);
//
//	        // 計算檢查碼
//	        String computedCheckMac = EcpayUtil.calculateCheckMacValue(rawString);
//	        logger.info("計算出的 CheckMacValue: {}", computedCheckMac);
//
//	        // 取得 ECPay 回傳的檢查碼
//	        String receivedCheckMac = request.getParameter("CheckMacValue");
//	        logger.info("收到的 CheckMacValue: {}", receivedCheckMac);
//
//	        // 比對檢查碼
//	        if (!computedCheckMac.equals(receivedCheckMac)) {
//	            throw new Exception("檢查碼驗證失敗");
//	        }
	        
	        // 取得所有參數（注意：你原本的 mergeRequestParameters 方法會過濾掉 bidingcard）
	        Map<String, String> paramMap = EcpayUtil.mergeRequestParameters(request);
	        
	        // 如果需要轉成 Hashtable (你也可以直接自行組一個 Hashtable)
	        Hashtable<String, String> paramTable = new Hashtable<>();
	        paramTable.putAll(paramMap);

	        // 使用 compareCheckMacValue 方法檢查檢查碼
	        boolean valid = allInOne.compareCheckMacValue(paramTable); // 假設你將 compareCheckMacValue 寫在 EcpayFunction 裡
	        if (!valid) {
	            throw new Exception("檢查碼驗證失敗");
	        }
	        
	        
	        
	        
//			// 驗證回傳資料
//			Object feedback = allInOne.aioCheckOutFeedback(request);

			String rtnCode = request.getParameter("RtnCode");
			String merchantTradeNo = request.getParameter("MerchantTradeNo");
			logger.info("收到綠界回傳，MerchantTradeNo: {}, RtnCode: {}", merchantTradeNo, rtnCode);

			if ("1".equals(rtnCode)) {
				if (merchantTradeNo != null && !merchantTradeNo.trim().isEmpty()) {
					// 根據 merchantTradeNo 更新訂單狀態
					ordersService.updateOrderStatusByMerchantTradeNo(merchantTradeNo, "已付款");
					logger.info("訂單 {} 狀態更新為 已付款", merchantTradeNo);
				} else {
					logger.warn("回傳參數中未包含 MerchantTradeNo，無法更新訂單狀態");
				}
			} else {
				logger.warn("付款失敗或狀態異常，RtnCode: {}", rtnCode);
			}
		} catch (Exception e) {
			logger.error("綠界金流回傳驗證失敗：{}", e.getMessage(), e);
		} finally {
			// 回應綠界系統要求的固定字串 "1|OK"
			response.getWriter().write("1|OK");

		}
	}

	@GetMapping("/test")
	public String testEndpoint() {
		return "PaymentReturnRestController is working";
	}

}
