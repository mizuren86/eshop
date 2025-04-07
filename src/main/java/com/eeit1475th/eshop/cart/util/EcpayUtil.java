package com.eeit1475th.eshop.cart.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

public class EcpayUtil {
    
    // 請根據你的設定填入正確的 HashKey 與 HashIV
    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private static final String HASH_IV  = "EkRm7iFT261dpevs";

    // 合併 Query String 與 POST Body 的參數
    public static Map<String, String> mergeRequestParameters(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> allParams = new HashMap<>();

        // 1. 取得 Query String 參數
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0];
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                allParams.put(key, value);
            }
        }
        
        // 2. 取得 POST 參數（注意 request.getParameterMap() 會自動合併 query string 與 body 參數，但為了明確，這裡再次加入）
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                allParams.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return allParams;
    }

    // 根據 ECPay 規範重建原始字串供 CheckMacValue 計算
    public static String buildRawStringForCheckMac(Map<String, String> params) throws UnsupportedEncodingException {
        // 移除不參與計算的參數（例如 CheckMacValue）
        params.remove("CheckMacValue");

        // 建立一個新的 Map，把所有 key 轉為小寫
        Map<String, String> lowerParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // 將所有鍵排序（按照字母順序）
        List<String> keys = new ArrayList<>(lowerParams.keySet());
        Collections.sort(keys);

        // 建立原始字串（未進行 URL encode），格式為：
        // HashKey=<HASH_KEY>&<sorted_key>=<value>&...&HashIV=<HASH_IV>
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(HASH_KEY);
        for (String key : keys) {
            String value = lowerParams.get(key);
            sb.append("&")
              .append(key)  // 此處 key 已經轉成小寫
              .append("=")
              .append(value);
        }
        sb.append("&HashIV=").append(HASH_IV);

        String rawString = sb.toString();
        // 根據官方要求：將整個字串 URL encode，再轉為小寫
        String encodedString = URLEncoder.encode(rawString, "UTF-8").toLowerCase();

        return encodedString;
    }


    // 以 SHA256 計算檢查碼並轉成大寫十六進制字串
    public static String calculateCheckMacValue(String rawString) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(rawString.getBytes("UTF-8"));

        // 將 byte 轉成大寫十六進制字串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}
