package com.minibot.macros.zulrah.phase;

import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum Stage {
    INITIAL(SnakeType.RANGE, 4, 3),

    MELEE_EAST(SnakeType.MELEE, 4, 3),
    MELEE_WEST(SnakeType.MELEE, -5, 0),

    MAGIC_EAST(SnakeType.MAGIC, 4, 3),
    MAGIC_SOUTH_EAST(SnakeType.MAGIC, 5, -3),
    MAGIC_SOUTH_WEST(SnakeType.MAGIC, -6, -2),

    RANGE_EAST(SnakeType.RANGE, 4, 3),
    RANGE_WEST(SnakeType.RANGE, -4, 3),
    RANGE_SOUTH_EAST(SnakeType.RANGE, 5, -3),
    RANGE_SOUTH_WEST(SnakeType.RANGE, -6, -3),

    JAD_EAST(SnakeType.JAD_MAGIC_FIRST, 4, -3),
    JAD_WEST(SnakeType.JAD_RANGE_FIRST, -6, -2);

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
        Tile origin = Zulrah.getOrigin();
        if (origin == null) {
            return null;
        }
        return origin.derive(offsetX, offsetY);
    }

}
