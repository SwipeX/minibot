package com.minibot.api.method;

import com.minibot.Minibot;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class RuneScape {

    /*public static void processAction(Action action, String actionText, String targetText, int x, int y) {
        try {
            System.out.println(action);
            System.out.println(String.format("^ %s, %s, %s, %s, %s, %s, %s, %s", action.arg0, action.arg1, action.arg2,
                    action.opcode, actionText, targetText, x, y));
            Mouse.hop(x, y);
            ModScript.serveInvoke("Client#processAction").invokeStatic(new Class<?>[]{
                    int.class, int.class, int.class, int.class, String.class, String.class, int.class, int.class
            }, new Object[]{
                    action.arg0, action.arg1, action.arg2, action.opcode, actionText, targetText, x, y
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Deprecated
    public static void processAction(int arg0, int arg1, int arg2, int opcode, String actionText, String targetText,
                                     int x, int y) {
        Minibot.instance().client().processAction(arg1, arg2, opcode, arg0, actionText, targetText, x, y);
    }
}
