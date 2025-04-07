package com.eeit1475th.eshop.cart.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class ECPayCheckMacValueUtil {

    /**
     * 依照綠界規範產生 CheckMacValue
     *
     * 規範步驟：
     * 1. 參數排序：依照第一個英文字母（不分大小寫）排序，並以 & 串連參數
     *    例：
     *    ChoosePayment=ALL&EncryptType=1&ItemName=Apple iphone 15&MerchantID=3002607&
     *    MerchantTradeDate=2023/03/12 15:30:23&MerchantTradeNo=ecpay20230312153023&
     *    PaymentType=aio&ReturnURL=https://www.ecpay.com.tw/receive.php&
     *    TotalAmount=30000&TradeDesc=促銷方案
     *
     * 2. 在最前面加上 "HashKey={hashKey}&"，最後加上 "&HashIV={hashIV}"
     * 3. 將整個字串進行 URL encode（採用 UTF-8，並依照 RFC 1866 編碼），
     *    範例結果：
     *    HashKey%3dpwFHCqoQZGmho4w6%26choosepayment%3dall%26encrypttype%3d1%26itemname%3dapple+iphone+15%26merchantid%3d3002607%26merchanttradedate%3d2023%2f03%2f12+15%3a30%3a23%26merchanttradeno%3decpay20230312153023%26paymenttype%3daio%26returnurl%3dhttps%3a%2f%2fwww.ecpay.com.tw%2freceive.php%26totalamount%3d30000%26tradedesc%3d%e4%bf%83%e9%8a%b7%e6%96%b9%e6%a1%88%26hashiv%3dekrm7ift261dpevs
     *
     * 4. 轉為小寫
     * 5. 以 SHA-256 計算雜湊
     * 6. 最後轉成大寫 HEX 字串即為 CheckMacValue
     *
     * @param hashKey  綠界提供的 HashKey
     * @param hashIV   綠界提供的 HashIV
     * @param params   傳入的參數 Map（包含 TradeDesc、PaymentType、MerchantTradeDate、MerchantTradeNo、MerchantID、
     *                 ReturnURL、ItemName、TotalAmount、ChoosePayment、EncryptType 等，不包含 CheckMacValue）
     * @return         產生的 CheckMacValue
     * @throws Exception
     */
    public static String generateCheckMacValue(String hashKey, String hashIV, Map<String, String> params) throws Exception {
        // 1. 過濾並依照不分大小寫排序參數（TreeMap 的排序依照鍵的自然排序）
        // 注意：參數排序的依據為參數名稱，不同語言排序結果可能略有差異，這裡假設符合綠界規範
        TreeMap<String, String> sortedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedParams.putAll(params);
        
        // 2. 依照格式組合參數字串 (用 & 串連，不需要額外加 & 在最前頭)
        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            // 每筆參數組成 "Key=Value"
            paramBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // 移除最後一個 &
        if(paramBuilder.length() > 0) {
            paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        }
        
        // 3. 在參數字串最前面加上 HashKey=，最後加上 &HashIV=
        String rawString = "HashKey=" + hashKey + "&" + paramBuilder.toString() + "&HashIV=" + hashIV;
        System.out.println("Raw string: " + rawString);
        
        // 4. URL encode 後轉成小寫 (使用 UTF-8)
        String urlEncoded = urlEncode(rawString).toLowerCase();
        System.out.println("URL Encoded (lowercase): " + urlEncoded);
        
        // 5. 依照 .NET 規則進行字串替換（強制轉換安全字元）
        String netEncoded = netUrlEncode(urlEncoded);
        System.out.println("NetEncoded string: " + netEncoded);
        
        // 6. 以 SHA-256 加密後轉成大寫 HEX 字串
        String sha256Hash = sha256(netEncoded).toUpperCase();
        System.out.println("Computed CheckMacValue: " + sha256Hash);
        
        return sha256Hash;
    }
    
    /**
     * 使用 Java 的 URLEncoder 對字串進行編碼
     */
    public static String urlEncode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, "UTF-8");
    }
    
    /**
     * 依照 .NET 規則進行轉換
     * （補充：若使用 PHP 請用 str_replace() 搭配 urlencode() 轉換表，
     * 這裡用 Java 模擬對 :、/、- 等進行強制替換）
     */
    public static String netUrlEncode(String url) {
        return url.replaceAll("%21", "!")
                  .replaceAll("%2a", "*")
                  .replaceAll("%28", "(")
                  .replaceAll("%29", ")")
                  .replaceAll("%2d", "-")
                  .replaceAll("%2e", ".")
                  .replaceAll("%5f", "_")
                  .replaceAll("%7e", "~")
                  .replaceAll("%3a", ":")
                  .replaceAll("%2f", "/")
                  .replaceAll("%20", "+");
    }
    
    /**
     * 使用 SHA-256 算法計算雜湊，並以 HEX 字串返回
     */
    public static String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    // 測試用 main 方法
    public static void main(String[] args) {
        try {
            // 建立參數 Map (依照範例順序，最終排序會自動依字母排序)
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("TradeDesc", "促銷方案");
            params.put("PaymentType", "aio");
            params.put("MerchantTradeDate", "2023/03/12 15:30:23");
            params.put("MerchantTradeNo", "ecpay20230312153023");
            params.put("MerchantID", "3002607");
            params.put("ReturnURL", "https://www.ecpay.com.tw/receive.php");
            params.put("ItemName", "Apple iphone 15");
            params.put("TotalAmount", "30000");
            params.put("ChoosePayment", "ALL");
            params.put("EncryptType", "1");
            
            String hashKey = "pwFHCqoQZGmho4w6";
            String hashIV = "EkRm7iFT261dpevs";
            
            String checkMacValue = generateCheckMacValue(hashKey, hashIV, params);
            System.out.println("CheckMacValue: " + checkMacValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
