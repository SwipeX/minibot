package com.minibot.macros.zulrah.phase;

import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Prayer;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * TODO add in Mystic Might / Eagle eye, fill in animations
 */
public enum SnakeType {
    RANGE(2042), MELEE(2043), MAGIC(2044), JAD_MAGIC_FIRST(-1), JAD_RANGE_FIRST(-2);
    private int id;

    SnakeType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public int[] getPrayerComponentIndices() {
        if (this.id() == JAD_MAGIC_FIRST.id()) {
            if (Zulrah.attackCounter % 2 == 0) {//first, third...
                System.out.println("MAGIC_JAD 0");
                return new int[]{Prayer.PROTECT_MAGIC_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
            } else {
                System.out.println("MAGIC_JAD 1");
                return new int[]{Prayer.PROTECT_RANGE_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
            }
        } else if (this.id() == JAD_RANGE_FIRST.id()) {
            if (Zulrah.attackCounter % 2 == 1) {//first, third...
                System.out.println("RANGE_JAD 0");
                return new int[]{Prayer.PROTECT_MAGIC_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
            } else {
                System.out.println("RANGE_JAD 1");
                return new int[]{Prayer.PROTECT_RANGE_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
            }
        } else if (this.id() == MELEE.id()) {
            return null;
        } else if (this.id() == MAGIC.id()) {
            return new int[]{Prayer.PROTECT_MAGIC_INDEX, Prayer.EAGLE_EYE_INDEX};
        }
        return new int[]{Prayer.PROTECT_RANGE_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
    }

    public void setId(int id) {
        this.id = id;
    }

}
