package com.minibot.macros.zulrah.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Price {

    private static URLConnection con;
    private static InputStream is;
    private static InputStreamReader isr;
    private static BufferedReader br;

    private static String[] getData(int itemID) {
        try {
            URL url = new URL(
                    "https://api.rsbuddy.com/grandExchange?a=guidePrice&i="
                            + itemID);
            con = url.openConnection();
            is = con.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = br.readLine();
            if (line != null) {
                return line.split(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                } else if (isr != null) {
                    isr.close();
                } else if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int getPrice(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[0].replaceAll("\\D", ""));
        }
        return 0;
    }

    public static int getAverageBuyOffer(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[1].replaceAll("\\D", ""));
        }
        return 0;
    }

    public static int getAverageSellOffer(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[3].replaceAll("\\D", ""));
        }
        return 0;
    }
} 