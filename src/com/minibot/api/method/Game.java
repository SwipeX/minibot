package com.minibot.api.method;

import com.minibot.mod.ModScript;
import com.minibot.mod.reflection.FieldValue;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Game {

    public static final int STATE_CREDENTIALS = 10;
    public static final int STATE_PLAYING = 25;
    public static final int STATE_IN_GAME = 30;

    private static FieldValue hook(String hookName) {
        return ModScript.hook("Client#" + hookName);
    }

    public static int state() {
        return hook("gameState").getInt();
    }

    public static int baseX() {
        return hook("baseX").getInt();
    }

    public static int baseY() {
        return hook("baseY").getInt();
    }

    public static int plane() {
        return hook("plane").getInt();
    }

    public static int[] settings() {
        try {
            return (int[]) hook("gameSettings").get();
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static int getSetting(int index) {
        int[] settings = settings();
        return settings.length == 0 || index >= settings.length ? -1 : settings[index];
    }

    public static void resetMouseIdleTime() {
        hook("mouseIdleTime").set(null, 0);
        System.out.println("Mouse idle: " + hook("mouseIdleTime").getInt());
    }
}
