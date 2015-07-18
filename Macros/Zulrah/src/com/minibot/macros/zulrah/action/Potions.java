package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.macros.zulrah.Zulrah;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Potions {

    private static final int FIVE_MINUTES = 1000 * 60 * 5;
    private static final int THREE_MINUTES = 1000 * 60 * 3;

    public static void reset() {
        for (Potion potion : Potion.values()) {
            potion.lastDrink = -1;
        }
    }

    public static void drink() {
        if (Zulrah.getMonster() == null) {
            return;
        }
        for (Potion potion : Potion.values()) {
            potion.drink();
        }
    }

    public enum Potion {
        MAGIC(Skills.MAGIC, FIVE_MINUTES),
        PRAYER(Skills.PRAYER, -1),
        RANGING(Skills.RANGED, FIVE_MINUTES),
        RESTORE(Skills.PRAYER, -1),
        VENOM(-1, THREE_MINUTES);

        private int skill;
        private int lifetime;
        private long lastDrink = -1;


        Potion(int skill, int lifetime) {
            this.skill = skill;
            this.lifetime = lifetime;
        }

        public int getLifetime() {
            return lifetime;
        }

        public long getLastDrink() {
            return lastDrink;
        }

        public void setLastDrink(long lastDrink) {
            this.lastDrink = lastDrink;
        }

        public Item get() {
            return Inventory.first(item -> item.name().toLowerCase().contains(name().toLowerCase()));
        }

        public void drink() {
            if (required()) {
                Item item = get();
                if (item != null) {
                    int id = item.id();
                    item.processAction("Drink");
                    lastDrink = System.currentTimeMillis();
                    Time.sleep(() -> Inventory.first(i -> i.id() == id) == null && Players.local().animation() == -1, 2500);
                }
            }
        }

        private boolean required() {
            boolean prayer = (skill == Skills.PRAYER &&
                    Game.realLevels()[skill] - Game.levels()[skill] > 20);
            boolean timing = (lastDrink == -1 ||
                    (System.currentTimeMillis() - lastDrink > lifetime));
            return prayer || (skill != Skills.PRAYER && timing);
        }
    }
}
