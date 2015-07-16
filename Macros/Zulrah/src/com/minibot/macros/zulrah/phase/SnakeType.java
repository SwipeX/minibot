package com.minibot.macros.zulrah.phase;

import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Prayer;
import com.minibot.macros.zulrah.util.Capture;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * TODO add in Mystic Might / Eagle eye, fill in animations
 */
public enum SnakeType {
    RANGE(2042), MELEE(2043), MAGIC(2044), JAD(-1);
    private static final int RANGE_ANIMATION = 0;
    private static final int MAGIC_ANIMATION = 0;
    private int id;

    SnakeType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public int[] getPrayerComponentIndices() {
        if (this.id() == JAD.id()) {
            Capture capture = Zulrah.getCapture();
            int animation = capture.getPreviousAnimation();
            if (animation == RANGE_ANIMATION) {
                return new int[]{Prayer.PROTECT_MAGIC_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
            } else {
                return new int[]{Prayer.PROTECT_RANGE_INDEX, Prayer.EAGLE_EYE_INDEX};
            }
        } else if (this.id() == MELEE.id()) {
            return null;
        }
        return this.id() == RANGE.id() ? new int[]{Prayer.PROTECT_RANGE_INDEX, Prayer.EAGLE_EYE_INDEX} :
                new int[]{Prayer.PROTECT_MAGIC_INDEX, Prayer.MYSTIC_MIGHT_INDEX};
    }

    public static SnakeType get(int id) {
        if (id == RANGE.id())
            return RANGE;
        if (id == MAGIC.id())
            return MAGIC;
        return MELEE;
    }
}