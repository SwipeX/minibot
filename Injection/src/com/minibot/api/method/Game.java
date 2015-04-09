package com.minibot.api.method;

import com.minibot.Minibot;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Game {

    public static final int STATE_CREDENTIALS = 10;
    public static final int STATE_PLAYING = 25;
    public static final int STATE_IN_GAME = 30;

    public static int state() {
        return Minibot.instance().client().getGameState();
    }

    public static int baseX() {
        return Minibot.instance().client().getBaseX();
    }

    public static int baseY() {
        return Minibot.instance().client().getBaseY();
    }

    public static int plane() {
        return Minibot.instance().client().getBaseY();
    }

    public static int[] settings() {
        int[] settings = Minibot.instance().client().getGameSettings();
        return settings == null ? null : settings.clone();
    }

    public static int setting(int index) {
        int[] settings = settings();
        return settings.length == 0 || index >= settings.length ? -1 : settings[index];
    }
}
