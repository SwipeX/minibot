package com.minibot.macros.zulrah;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum SnakeType {
    RANGE(2042), MELEE(2043), MAGIC(2044), JAD(-1);

    private int id;

    SnakeType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
