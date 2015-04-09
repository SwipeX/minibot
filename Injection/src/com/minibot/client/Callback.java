package com.minibot.client;

import com.minibot.client.ClientInvoked;

/**
 * Project: minibot
 * Date: 07-04-2015
 * Time: 00:33
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        System.out.println("Arg1: " + arg1 + ", arg2: " + arg2 + ", opcode: " + op + ", arg0: " + arg0
                + ", action: " + action + ", target: " + target + ", x: " + x + ", y: " + y);
    }
}
