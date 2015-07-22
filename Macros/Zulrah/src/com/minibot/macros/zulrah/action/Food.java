package com.minibot.macros.zulrah.action;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Player;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Food {

    public static void eat() {
        Player local = Players.local();
        if (Minibot.instance().client().getLevels()[Skills.HITPOINTS] <= 41) {
            Item food = Inventory.firstFood();
            if (food != null) {
                food.processAction("Eat");
            }
        }
    }
}
