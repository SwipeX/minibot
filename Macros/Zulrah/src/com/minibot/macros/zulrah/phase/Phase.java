package com.minibot.macros.zulrah.phase;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * TODO phase 1 & 4...wtf?
 */
public enum Phase {
    PHASE_1(Stage.INITIAL, Stage.MELEE_EAST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MELEE_WEST,
            Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_EAST, Stage.MAGIC_SOUTH_WEST, Stage.JAD_WEST,
            Stage.MELEE_WEST, Stage.RANGE_WEST),

    PHASE_2(Stage.INITIAL, Stage.MELEE_EAST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_EAST,
            Stage.MELEE_EAST, Stage.RANGE_SOUTH_EAST, Stage.MAGIC_SOUTH_WEST, Stage.JAD_WEST, Stage.MELEE_WEST,
            Stage.RANGE_WEST),

    PHASE_3(Stage.INITIAL, Stage.RANGE_EAST, Stage.MELEE_WEST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_EAST,
            Stage.MAGIC_SOUTH_EAST, Stage.RANGE_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_EAST, Stage.JAD_EAST),

    PHASE_4(Stage.INITIAL, Stage.MAGIC_EAST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_WEST, Stage.MELEE_EAST,
            Stage.RANGE_SOUTH_EAST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_EAST, Stage.MAGIC_EAST,
            Stage.JAD_EAST, Stage.MAGIC_EAST);

    Stage[] stages;
    int index = 0;
    boolean confirmed = false;

    Phase(Stage... stages) {
        this.stages = stages;
    }

    public void advance() {
        index++;
        if (index >= stages.length)
            index = 1; // prevent initial from occuring more than once.
    }

    public void backup() {
        if (index >= 1) {
            index--;
        } else {
            index = stages.length - 1;
        }
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
            if (phase.matches(previous, current)) {
                if (selected == null) {
                    selected = phase;
                } else {
                    return null;
                    //we already have a phase determined....conflict...abort
                }
            }
        }
        return selected;
    }

    public boolean matches(ArrayList<Integer> previous, int current) {
        for (int i = 0; i < previous.size(); i++) {
            Stage stage = stages[i];
            int stageId = stage.getSnakeType().id();
            int prevId = previous.get(i);
            if (stageId != prevId)
                return false;
        }
        return stages[previous.size()].getSnakeType().id() == current; // why not return true; ?
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void draw(Graphics g, int x, int y) {
        String phase = "";
        String phase2 = "";
        for (int i = 0; i < stages.length; i++) {
            Stage stage = stages[i];
            if (i < 5)
                phase += stage.name() + "[" + (index > i ? "X" : "") + "] ";
            else
                phase2 += stage.name() + "[" + (index > i ? "X" : "") + "] ";
        }
        g.drawString(name() + ": " + phase, x, y);
        g.drawString(phase2, x, y += 13);
    }

    public void unconfirm() {
        confirmed = false;
    }
}
