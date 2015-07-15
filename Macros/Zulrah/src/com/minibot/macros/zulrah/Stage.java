package com.minibot.macros.zulrah;

import com.minibot.api.wrapper.locatable.Tile;

import java.awt.datatransfer.DataFlavor;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum Stage {
    INITIAL(SnakeType.RANGE, 0, 0),

    MELEE_WEST(SnakeType.MELEE, 0, 0),
    MELEE_EAST(SnakeType.MELEE, 0, 0),

    MAGIC_WEST(SnakeType.MAGIC, 0, 0),
    MAGIC_NORTHWEST(SnakeType.MAGIC, 0, 0),
    MAGIC_EAST(SnakeType.MAGIC, 0, 0),

    RANGE_WEST(SnakeType.RANGE, 0, 0),
    RANGE_NORTHWEST(SnakeType.RANGE, 0, 0),
    RANGE_NORTHEAST(SnakeType.RANGE, 0, 0),

    JAD_WEST(SnakeType.JAD, 0, 0),
    JAD_EAST(SnakeType.JAD, 0, 0);

    SnakeType snakeType;
    int offsetX, offsetY;

    Stage(SnakeType snakeType, int offsetX, int offsetY) {
        this.snakeType = snakeType;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Tile getTile() {
        return new Tile(offsetX,offsetY); // derive from initial tile
    }
}
