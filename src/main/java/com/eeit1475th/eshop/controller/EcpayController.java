package com.eeit1475th.eshop.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ecpay.logistics.integration.AllInOne;
import ecpay.logistics.integration.domain.ExpressMapObj;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class EcpayController {

    @GetMapping("/ecpay/expressMap")
    public void expressMap(HttpServletResponse response) throws IOException {
        // 傳入配置檔所在的路徑（確保配置檔放在對應的 classpath 下）
        AllInOne allInOne = new AllInOne("/Users/cranny/eeit145-5th/eshop/target/classes/");
        ExpressMapObj mapObj = new ExpressMapObj();

        // 設定必填參數
        mapObj.setMerchantID("2000132");               // 廠商編號，請根據實際情況設定
        mapObj.setMerchantTradeNo("TestTradeNo001");
        mapObj.setLogisticsSubType("UNIMART");              // 必填，選項之一，例如 FAMI
        mapObj.setIsCollection("N");                     // 必填，通常設為 "N"
        mapObj.setServerReplyURL("https://smooth-ends-cover.loca.lt/ecpay/callback");

        // 取得綠界電子地圖的 HTML 表單，該表單會自動提交
        String htmlForm = allInOne.expressMap(mapObj);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(htmlForm);
    }
}
