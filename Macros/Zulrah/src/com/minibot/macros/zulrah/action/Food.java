package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Food {

    public static void eat() {
        if (Players.local().health() < 41) {
            Item food = Inventory.firstFood();
            if (food != null) {
                food.processAction("Eat");
                //possible karambwan combo to save time
                //possible prayer potion combo-drink to save time
                Time.sleep(400, 500);
            }
        }
    }

}
