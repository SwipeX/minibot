package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;

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
        return Minibot.instance().client().getPlane();
    }

    public static int[] realLevels() {
        return Minibot.instance().client().getRealLevels();
    }

    public static int[] levels() {
        return Minibot.instance().client().getLevels();
    }

    public static int[] experiences() {
        return Minibot.instance().client().getExperiences();
    }

    public static int[] varps() {
        int[] settings = Minibot.instance().client().getGameSettings();
        return settings == null ? null : settings.clone();
    }

    public static int varp(int index) {
        int[] settings = varps();
        return settings.length == 0 || index >= settings.length ? -1 : settings[index];
    }

    public static boolean isLoggedIn() {
        return Minibot.instance().client().getGameState() == STATE_IN_GAME;
    }

    public static Character getHinted() {
        int npcIndex = Minibot.instance().client().getHintNpcIndex();
        Npc npc = Npcs.atIndex(npcIndex);
        int playerIndex = Minibot.instance().client().getHintPlayerIndex();
        Player player = Players.atIndex(playerIndex);
        return npc != null ? npc : (player != null ? player : null);
    }
}
