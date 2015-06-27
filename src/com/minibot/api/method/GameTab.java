package com.minibot.api.method;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;

/**
 * @author Tyler Sedlar
 * @since Oct 16, 2014
 */
public enum GameTab {

    CLAN_CHAT("Clan Chat"),
    FRIENDS_LIST("Friends List"),
    IGNORE_LIST("Ignore List"),
    LOGOUT("Logout"),
    OPTIONS("Options"),
    EMOTES("Emotes"),
    MUSIC("Music Player"),
    COMBAT("Combat Options"),
    STATS("Stats"),
    QUESTS("Quest List"),
    INVENTORY("Inventory"),
    EQUIPMENT("Worn Equipment"),
    PRAYER("Prayer"),
    MAGIC("Magic");

    private static final int PARENT = 548;
    private final String action;

    GameTab(String action) {
        this.action = action;
    }

    public int componentIndex() {
        return ordinal() <= 6 ? 25 + ordinal() : 35 + ordinal();
    }

    public WidgetComponent component() {
        return Widgets.get(PARENT, componentIndex());
    }

    public boolean viewing() {
        WidgetComponent component = component();
        return component != null && component.textureId() != -1;
    }

    public boolean open() {
        WidgetComponent component = component();
        if (component != null) {
            if (component.textureId() == -1) {
                component.processAction(ActionOpcodes.WIDGET_ACTION, 1, action, "");
                Time.sleep(150, 300);
            }
            return viewing();
        }
        return false;
    }

    public static GameTab current() {
        for (GameTab tab : GameTab.values()) {
            if (tab.viewing())
                return tab;
        }
        return null;
    }
}