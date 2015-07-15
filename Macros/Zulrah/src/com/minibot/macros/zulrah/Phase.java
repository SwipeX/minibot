package com.minibot.macros.zulrah;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public enum Phase {
    PHASE_1(Stage.INITIAL, Stage.MELEE_WEST, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST,
            Stage.MELEE_EAST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_NORTHWEST,
            Stage.JAD_EAST, Stage.MELEE_EAST),//jad range first

    PHASE_2(Stage.INITIAL, Stage.RANGE_WEST, Stage.MELEE_EAST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST,
            Stage.MAGIC_NORTHWEST, Stage.RANGE_NORTHEAST, Stage.RANGE_NORTHEAST, Stage.MAGIC_WEST,
            Stage.JAD_WEST, Stage.MAGIC_NORTHWEST),//jad mage first

    PHASE_3(Stage.INITIAL, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST, Stage.MAGIC_EAST, Stage.MELEE_WEST,
            Stage.RANGE_WEST, Stage.RANGE_NORTHWEST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_NORTHWEST,
            Stage.JAD_WEST, Stage.MAGIC_WEST),//magic first

    PHASE_4(Stage.INITIAL, Stage.MELEE_WEST, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST, Stage.MAGIC_EAST,
            Stage.MELEE_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_EAST, Stage.JAD_EAST, Stage.RANGE_WEST,
            Stage.MELEE_WEST);//range first

    Stage[] stages;

    Phase(Stage... stages) {
        this.stages = stages;
    }
}
