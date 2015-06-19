package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Widgets;

import static com.minibot.api.action.ActionOpcodes.*;

public abstract class ButtonAction extends Action {

    public ButtonAction(int opcode, int buttonIndex, int widgetUid) {
        super(opcode, 0, buttonIndex, widgetUid);
    }

    public ButtonAction(int opcode, int widgetUid) {
        this(opcode, 0, widgetUid);
    }

    public final int significantArgs() {
        return ARG1|ARG2;
    }

    // widgetUid is known to be the arg2
    public static ButtonAction valueOf(int opcode, int widgetUid) {
        switch (opcode) {
            case BUTTON_INPUT: {
                return new InputButtonAction(widgetUid);
            }
            case BUTTON_SPELL: {
                return new SpellButtonAction(widgetUid);
            }
            case BUTTON_CLOSE: {
                return new CloseButtonAction(widgetUid);
            }
            case BUTTON_VAR_FLIP: {
                return new VarFlipButtonAction(widgetUid);
            }
            case BUTTON_DIALOG: {
                return new DialogButtonAction(widgetUid);
            }
        }
        return null;
    }

    public static int buttonForOpcode(final int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.BUTTON_INPUT: {
                return Widgets.BUTTON_INPUT;
            }
            case ActionOpcodes.BUTTON_SPELL: {
                return Widgets.BUTTON_SPELL;
            }
            case ActionOpcodes.BUTTON_CLOSE: {
                return Widgets.BUTTON_CLOSE;
            }
            case ActionOpcodes.BUTTON_VAR_FLIP: {
                return Widgets.BUTTON_VAR_FLIP;
            }
            case ActionOpcodes.BUTTON_VAR_SET: {
                return Widgets.BUTTON_VAR_SET;
            }
            case ActionOpcodes.BUTTON_DIALOG: {
                return Widgets.BUTTON_DIALOG;
            }
        }
        return -1;
    }

    public int buttonUid() {
        return arg2;
    }

    public int buttonParent() {
        return buttonUid() >> 16;
    }

    public int buttonChild() {
        return buttonUid() & 0xffff;
    }

    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg2 == arg2;
    }
}
