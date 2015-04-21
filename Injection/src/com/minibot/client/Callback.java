package com.minibot.client;

import com.minibot.api.action.tree.Action;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        System.out.println(Action.valueOf(op, arg0, arg1, arg2));
    }
}
