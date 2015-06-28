package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
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
    public static final int HITPOINTS = 4;
    public static final int PRAYER = 14;
    public static final int RUN_PERCENT = 22;

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

    public static int totalExperience() {
        int i = 0;
        for (int exp : experiences())
            i += exp;
        return i;
    }

    public static boolean playing() {
        return state() >= STATE_PLAYING; // doesn't need widget check, since lobby = 10
    }

    public static int data(int index) {
        WidgetComponent component = Widgets.get(160, index);
        if (component != null && playing()) {
            String text = component.text();
            return text != null ? Integer.parseInt(text) : -1;
        }
        return -1;
    }

    public static boolean runEnabled() {
        WidgetComponent run = Widgets.get(160, 23);
        return run != null && playing() && run.textureId() == 1065;
    }

    public static boolean setRun() {
        WidgetComponent component = Widgets.get(160, 21);
        if (component != null && playing()) {
            if (!runEnabled()) {
                component.processAction("Toggle Run");
            }
            return Time.sleep(Game::runEnabled, Random.nextInt(1500, 2000));
        }
        return false;
    }

    public static int[] varps() {
        int[] settings = Minibot.instance().client().getGameSettings();
        return settings == null ? null : settings.clone();
    }

    public static int varp(int index) {
        int[] settings = varps();
        return settings.length == 0 || index >= settings.length ? -1 : settings[index];
    }

    public static Character getHinted() {
        int npcIndex = Minibot.instance().client().getHintNpcIndex();
        Npc npc = Npcs.atIndex(npcIndex);
        int playerIndex = Minibot.instance().client().getHintPlayerIndex();
        Player player = Players.atIndex(playerIndex);
        return npc != null ? npc : (player != null ? player : null);
    }
}