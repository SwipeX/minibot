package com.minibot.macros.zulrah.action;

import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;

import java.util.Deque;

/**
 * @author Tim Dekker
 * @since 7/24/15
 */
public class Teleport {

    private static final int PARENT = 219;
    private static final int CHILD = 0;
    private static final int SUB_CHILD = 3;

    public static void handle() {
        if (Zulrah.monster() == null) {
            if (Zulrah.origin() != null && Zulrah.origin().distance() < 10) {
                Deque<GroundItem> items = Ground.loaded(20);
                if (items.isEmpty()) {
                    act();
                }
            }
        }
    }

    private static void act() {
        Item ring = Inventory.first(item -> item.name().contains("dueling"));
        if (ring != null) {
            ring.processAction("Rub");
            Time.sleep(() -> clanComponent() != null, 2000);
            WidgetComponent clan = clanComponent();
            if (clan != null) {
                Tile location = Players.local().location();
                RuneScape.processAction(new DialogButtonAction(14352384, 3), "", "");
                Time.sleep(() -> Players.local().location().x() != location.x(), 5000);
            }
        } else {
            System.out.println("No dueling ring...what the...");
        }
    }

    private static WidgetComponent clanComponent() {
        WidgetComponent clan = Widgets.get(PARENT, CHILD);
        if (clan != null) {
            return clan.child(i -> i.index() == SUB_CHILD);
        }
        return null;
    }
}