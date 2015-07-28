package com.minibot.macros.zulrah.util;

import com.minibot.util.io.Internet;

import java.util.HashMap;
import java.util.Map;

public class Price {

    private static final Map<Integer, Integer> PRICE_CACHE = new HashMap<>();

    private static String[] getData(int itemId) {
        String line = Internet.readFully("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemId);
        if (line != null) {
            return line.split(",");
        } else {
            return null;
        }
    }

    public static int lookup(int itemId) {
        if (PRICE_CACHE.containsKey(itemId)) {
            return PRICE_CACHE.get(itemId);
        }
        int amount = 0;
        String[] data = getData(itemId);
        if (data != null && data.length == 5) {
            amount = Integer.parseInt(data[0].replaceAll("\\D", ""));
            if (amount <= 0) {
                data = getData(itemId - 1);
                if (data != null) {
                    amount = Integer.parseInt(data[0].replaceAll("\\D", ""));
                }
            }
        }
        if (amount > 0) {
            PRICE_CACHE.put(itemId, amount);
        }
        return amount;
    }
} 