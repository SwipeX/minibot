package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class RuneScape {
   public static boolean MODEL_RENDERING_ENABLED = false;
    public static boolean LANDSCAPE_RENDERING_ENABLED = false;

    public static void processAction(Action action, String actionText, String targetText, int x, int y) {
        Minibot.instance().client().processAction(action.arg1, action.arg2, action.opcode, action.arg0,
                actionText, targetText, x, y);
    }

    @Deprecated
    public static void processAction(int arg0, int arg1, int arg2, int opcode, String actionText, String targetText,
                                     int x, int y) {
        Minibot.instance().client().processAction(arg1, arg2, opcode, arg0, actionText, targetText, x, y);
    }
}
