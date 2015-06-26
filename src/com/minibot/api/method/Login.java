package com.minibot.api.method;

import com.minibot.Minibot;

import java.awt.*;

 public class Login {
 
    public static final Point LOGIN = new Point(370, 325);
    public static final Point EXISTING_USER = new Point(465, 290);
    public static final int STATE_MAIN_MENU = 0;
    public static final int STATE_CREDENTIALS = 2;
 
    public static void setUsername(String to) {
         Minibot.instance().client().setUsername(to);
    }
 
    public static void setPassword(String to) {
         Minibot.instance().client().setPassword(to);
    }
 
    public static int state() {
        return Minibot.instance().client().getLoginState();
    }
}