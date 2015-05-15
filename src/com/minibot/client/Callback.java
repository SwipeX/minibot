package com.minibot.client;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;
import com.minibot.api.util.Time;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        System.out.println(Action.valueOf(op, arg0, arg1, arg2));
    }

    @ClientInvoked
    public static void onEngineTick() {
        if (Minibot.instance().isFarming())
            Time.sleep(80);
    }
}
