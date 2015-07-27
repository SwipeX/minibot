package com.minibot.macros.zulrah.phase;

import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Prayer;

import java.awt.Graphics;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 7/14/15
 * <p>
 * WORKING:
 * - PHASE_1 (Messes up if thinks is unconfirmed PHASE_4)
 * - PHASE_2
 * - PHASE_3
 * - PHASE_4
 */
public enum Phase {

    PHASE_1(Stage.INITIAL, Stage.MELEE_EAST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MELEE_WEST,
            Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_EAST, Stage.MAGIC_SOUTH_WEST, Stage.JAD_WEST, Stage.MELEE_EAST),

    PHASE_2(Stage.INITIAL, Stage.MELEE_EAST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_EAST,
            Stage.MELEE_EAST, Stage.RANGE_SOUTH_EAST, Stage.MAGIC_SOUTH_WEST, Stage.JAD_WEST, Stage.MELEE_EAST),

    PHASE_3(Stage.INITIAL, Stage.RANGE_EAST, Stage.MELEE_WEST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_SOUTH_EAST,
            Stage.MAGIC_SOUTH_EAST, Stage.RANGE_SOUTH_WEST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_EAST, Stage.JAD_EAST,
            Stage.MAGIC_EAST),

    PHASE_4(Stage.INITIAL, Stage.MAGIC_EAST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_WEST, Stage.MELEE_EAST,
            Stage.RANGE_SOUTH_EAST, Stage.RANGE_SOUTH_WEST, Stage.MAGIC_SOUTH_WEST, Stage.RANGE_EAST, Stage.MAGIC_EAST,
            Stage.JAD_EAST, Stage.MAGIC_EAST);

    final Stage[] stages;
    int index;
    boolean confirmed;

    Phase(Stage... stages) {
        this.stages = stages;
    }

    public int advance() {
        index++;
        if (index >= stages.length) {
            reset();
            Zulrah.previous().clear();
            Prayer.deactivateAll();
            Prayer.PROTECT_FROM_MISSILES.setActive(true);
            System.out.println("RESETTING PHASES");
        }
        return index;
    }

    public int index() {
        return index;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void confirm() {
        confirmed = true;
    }

    public static void reset() {
        Zulrah.previous().clear();
        for (Phase phase : values()) {
            phase.unconfirm();
            phase.index = 0;
        }
        Zulrah.resetPhase();
    }

    public Stage current() {
        return stages[index];
    }

    public static Phase determine(List<Integer> previous, int current) {
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

    public boolean matches(List<Integer> previous, int current) {
        for (int i = 0; i < previous.size(); i++) {
            Stage stage = stages[i];
            int stageId = stage.getSnakeType().id();
            int prevId = previous.get(i);
            if (stageId != prevId) {
                return false;
            }
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
            if (i < 5) {
                phase += stage.name() + "[" + (index > i ? "X" : "") + "] ";
            } else {
                phase2 += stage.name() + "[" + (index > i ? "X" : "") + "] ";
            }
        }
        g.drawString(name() + ": " + phase, x, y);
        g.drawString(phase2, x, y += 13);
    }

    public void unconfirm() {
        confirmed = false;
    }
}