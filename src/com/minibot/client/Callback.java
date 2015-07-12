package com.minibot.client;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;
import com.minibot.api.method.ChatboxListener;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Time;
import com.minibot.ui.MacroSelector;

public class Callback {

    @ClientInvoked
    public static void messageReceived(int type, String sender, String message, String clan) {
        if (MacroSelector.current() != null && MacroSelector.current() instanceof ChatboxListener)
            ((ChatboxListener) MacroSelector.current()).messageReceived(type, sender, message, clan);
    }

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        System.out.println(Action.valueOf(op, arg0, arg1, arg2));
    }

    @ClientInvoked
    public static void onEngineTick() {
        RuneScape.processActions();
        if (Minibot.instance().farming())
            Time.sleep(80);
    }
}