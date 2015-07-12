package com.minibot.macros.clue;

import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public enum TeleportLocation {
    VARROCK(16, new Tile(3212, 3422, 0)),
    LUMBRIDGE(19, new Tile(3219, 3219, 0)),
    FALADOR(22, new Tile(2967, 3377, 0)),
    CAMELOT(27, new Tile(2755, 3478, 0)),
    ARDOUGNE(33, new Tile(2660, 3303, 0));

    private static final int SPELL_BOOK = 218;

    public final int componentIndex;
    public final Tile destination;

    TeleportLocation(int componentIndex, Tile destination) {
        this.componentIndex = componentIndex;
        this.destination = destination;
    }

    public boolean teleport() {
        WidgetComponent component = Widgets.get(SPELL_BOOK, componentIndex);
        if (component != null) {
            component.processAction("Cast");
            return Time.sleep(() -> destination.distance() < 10, 15000);
        }
        return false;
    }
}
