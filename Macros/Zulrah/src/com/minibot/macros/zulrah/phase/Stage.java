package com.minibot.macros.zulrah.phase;

import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum Stage {
    INITIAL(SnakeType.RANGE, -4, -3),

    MELEE_WEST(SnakeType.MELEE, 0, 0),
    MELEE_EAST(SnakeType.MELEE, 5, 0),

    MAGIC_WEST(SnakeType.MAGIC, 0, 0),
    MAGIC_NORTHWEST(SnakeType.MAGIC, 0, 0),
    MAGIC_EAST(SnakeType.MAGIC, 6, 2),

    RANGE_WEST(SnakeType.RANGE, 0, 0),
    RANGE_NORTHWEST(SnakeType.RANGE, 0, 0),
    RANGE_NORTHEAST(SnakeType.RANGE, 0, 0),

    JAD_WEST(SnakeType.JAD, 0, 0),
    JAD_EAST(SnakeType.JAD, 6, 2);

    private SnakeType snakeType;
    private int offsetX, offsetY;

    Stage(SnakeType snakeType, int offsetX, int offsetY) {
        this.snakeType = snakeType;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public SnakeType getSnakeType() {
        return snakeType;
    }

    public Tile getTile() {
        return new Tile(offsetX, offsetY); // derive from initial tile
    }

}
