package com.minibot.api.method;

import com.minibot.api.wrapper.WidgetComponent;

/**
 * @author <a href="mailto:t@sedlar.me">Tyler Sedlar</a>
 * @since Oct 16, 2014 - 8:35 PM
 */
public enum GameTab {
    CLAN_CHAT, FRIENDS_LIST, IGNORE_LIST, LOGOUT, OPTIONS, EMOTES, MUSIC,
    COMBAT, STATS, QUESTS, INVENTORY, EQUIPMENT, PRAYER, MAGIC;

    public static final int PARENT = 548;

    public int componentIndex() {
        return ordinal() <= 6 ? (31 + ordinal()) : (48 + (ordinal() - 7));
    }

    public WidgetComponent component() {
        return Widgets.get(PARENT, componentIndex());
    }

    public boolean viewing() {
        WidgetComponent component = component();
        return component != null && component.textureId() != -1;
    }

    public void view() {
        WidgetComponent component = component();
        if (component != null) {
            String[] actions = component.actions();
            if (actions != null && actions.length > 0)
                component.processAction(component.actions()[0]);
        }
    }

    public static GameTab current() {
        for (GameTab tab : GameTab.values()) {
            if (tab.viewing())
                return tab;
        }
        return null;
    }
}
