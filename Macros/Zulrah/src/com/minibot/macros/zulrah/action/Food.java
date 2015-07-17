package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Player;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Food {

    public static void eat() {
        Player local = Players.local();
        if (local.maxHealth() > 0 && local.health() <= 41) {
            Item food = Inventory.firstFood();
            if (food != null) {
                food.processAction("Eat");
            }
        }
    }
}
