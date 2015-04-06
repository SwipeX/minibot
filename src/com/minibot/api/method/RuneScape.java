package com.minibot.api.method;

import com.minibot.internal.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class RuneScape {

    public static void doAction(int targetIndex, int unknown, int packetId, int targetId, String action,
                                String target, int x, int y) {
        try {
            System.out.println(targetIndex + ", " + unknown + ", " + packetId + ", " + targetId + ", " + action +
                    ", " + target + ", " + x + ", " + y);
            ModScript.serveInvoke("Client#doAction").invokeStatic(new Class<?>[]{
                    int.class, int.class, int.class, int.class, String.class, String.class, int.class, int.class
            }, new Object[]{
                    targetIndex, unknown, packetId, targetId, action, target, x, y
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
