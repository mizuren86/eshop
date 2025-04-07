package com.eeit1475th.eshop.util;

import java.util.Base64;

public class Base64Util {
    
	public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
