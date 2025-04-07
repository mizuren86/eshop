package com.eeit1475th.eshop.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ecpay.logistics.integration.AllInOne;
import ecpay.logistics.integration.domain.ExpressMapObj;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class EcpayController {

    @GetMapping("/ecpay/expressMap")
    public void expressMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	// 從 session 讀取運送與付款方式，若沒有則設定預設值
        String shippingMethod = (String) request.getSession().getAttribute("shippingMethod");
        if (shippingMethod == null || shippingMethod.trim().isEmpty()) {
            shippingMethod = "711-cod";
        }
        String paymentMethod = (String) request.getSession().getAttribute("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "credit";
        }
    	
        // 傳入配置檔所在的路徑（確保配置檔放在對應的 classpath 下）
        AllInOne allInOne = new AllInOne("/Users/cranny/eeit145-5th/eshop/target/classes/");
        ExpressMapObj mapObj = new ExpressMapObj();

        // 設定必填參數
        mapObj.setMerchantID("2000132");               // 廠商編號，請根據實際情況設定
        mapObj.setMerchantTradeNo("TestTradeNo001");
        mapObj.setLogisticsSubType("UNIMART");              // 必填，選項之一，例如 FAMI
        mapObj.setIsCollection("N");                     // 必填，通常設為 "N"
        // 將 shippingMethod 和 paymentMethod 加入 ServerReplyURL 的查詢字串中
        String callbackUrl = "https://7ab9-124-218-129-55.ngrok-free.app/ecpay/callback"
                + "?shippingMethod=" + shippingMethod
                + "&paymentMethod=" + paymentMethod;
        mapObj.setServerReplyURL(callbackUrl);
        

        // 取得綠界電子地圖的 HTML 表單，該表單會自動提交
        String htmlForm = allInOne.expressMap(mapObj);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(htmlForm);
    }
}
