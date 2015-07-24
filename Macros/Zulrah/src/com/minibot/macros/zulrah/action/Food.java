package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Food {

    private static long lastEatTime = -1;

    public static void eat() {
        if (lastEatTime != -1 && Time.millis() - lastEatTime < 150) {
            return;
        }
        if (Game.levels()[Skills.HITPOINTS] <= 45) {
            Item food = Inventory.firstFood();
            if (food != null) {
                food.processAction("Eat");
                lastEatTime = Time.millis();
            }
        }
    }
}