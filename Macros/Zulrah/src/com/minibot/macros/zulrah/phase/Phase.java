package com.minibot.macros.zulrah.phase;

import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * TODO phase 1 & 4...wtf?
 */
public enum Phase {
    PHASE_1(Stage.INITIAL, Stage.MELEE_WEST, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST,
            Stage.MELEE_EAST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_NORTHWEST,
            Stage.JAD_EAST, Stage.MELEE_EAST),

    PHASE_2(Stage.INITIAL, Stage.RANGE_WEST, Stage.MELEE_EAST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST,
            Stage.MAGIC_NORTHWEST, Stage.RANGE_NORTHEAST, Stage.RANGE_NORTHEAST, Stage.MAGIC_WEST,
            Stage.JAD_WEST, Stage.MAGIC_NORTHWEST),

    PHASE_3(Stage.INITIAL, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST, Stage.MAGIC_EAST, Stage.MELEE_WEST,
            Stage.RANGE_WEST, Stage.RANGE_NORTHWEST, Stage.MAGIC_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_NORTHWEST,
            Stage.JAD_WEST, Stage.MAGIC_WEST),

    PHASE_4(Stage.INITIAL, Stage.MELEE_WEST, Stage.MAGIC_WEST, Stage.RANGE_NORTHEAST,
            Stage.MAGIC_EAST, Stage.MELEE_EAST, Stage.RANGE_NORTHWEST, Stage.MAGIC_EAST,
            Stage.JAD_EAST, Stage.RANGE_WEST, Stage.MELEE_WEST);

    Stage[] stages;
    int index = 0;
    boolean confirmed = false;

    Phase(Stage... stages) {
        this.stages = stages;
    }

    public void advance() {
        index++;
        if (index >= stages.length)
            index = 0;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void confirm() {
        confirmed = true;
    }

    public static void reset() {
        for (Phase phase : values())
            phase.confirmed = false;
    }

    public Stage getCurrent() {
        return stages[index];
    }

    public static Phase determine(ArrayList<Integer> previous, int current) {
        Phase selected = null;
        for (Phase phase : values()) {
            boolean valid = true;
            Stage[] stages = phase.stages;
            for (int i = 0; i < previous.size(); i++) {
                if (stages[i].getSnakeType().id() != previous.get(i)) {
                    valid = false;
                }
            }
            if (valid) {
                if (stages[previous.size()].getSnakeType().id() == current) {
                    if (selected == null) {
                        selected = phase;
                    } else {
                        //System.out.println("Phase intersection -- aborting");
                        return null;
                    }
                }
            }
        }
        return selected;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
