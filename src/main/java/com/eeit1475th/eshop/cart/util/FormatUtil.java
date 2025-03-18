package com.eeit1475th.eshop.cart.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FormatUtil {
	// 格式模式 '$#,###' > $12,000
    private static final NumberFormat currencyFormat = new DecimalFormat("$#,###");

    public static String formatPrice(Number price) {
        return currencyFormat.format(price);
    }
}
