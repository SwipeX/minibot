package com.minibot.macros.zulrah.phase;

import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Prayer;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum SnakeType {

    RANGE(2042),
    MELEE(2043),
    MAGIC(2044),
    JAD_MAGIC_FIRST(-1),
    JAD_RANGE_FIRST(-2);

    private int id;

    SnakeType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public Prayer[] getPrayers() {
        if (this.id() == JAD_MAGIC_FIRST.id()) {
            if (Zulrah.projectileType == Zulrah.PROJECTILE_MAGE) {
                return new Prayer[]{Prayer.PROTECT_FROM_MISSILES, Prayer.MYSTIC_MIGHT};
            } else {
                return new Prayer[]{Prayer.PROTECT_FROM_MAGIC, Prayer.MYSTIC_MIGHT};
            }
        } else if (this.id() == JAD_RANGE_FIRST.id()) {
            if (Zulrah.projectileType == Zulrah.PROJECTILE_MAGE) {
                return new Prayer[]{Prayer.PROTECT_FROM_MISSILES, Prayer.MYSTIC_MIGHT};
            } else {
                return new Prayer[]{Prayer.PROTECT_FROM_MAGIC, Prayer.MYSTIC_MIGHT};
            }
        } else if (this.id() == MELEE.id()) {
            return null;
        } else if (this.id() == MAGIC.id()) {
            return new Prayer[]{Prayer.PROTECT_FROM_MAGIC, Prayer.EAGLE_EYE};
        }
        return new Prayer[]{Prayer.PROTECT_FROM_MISSILES, Prayer.MYSTIC_MIGHT};
    }

    public void setId(int id) {
        this.id = id;
    }
}