package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class SpellOnWidgetAction extends Action {

    public SpellOnWidgetAction(int widgetIndex, int widgetId) {
        super(ActionOpcodes.SPELL_ON_WIDGET, 0, widgetIndex, widgetId);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.SPELL_ON_WIDGET;
    }

    @Override
    public int significantArgs() {
        return ARG1 | ARG2;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg1 == arg1 && this.arg2 == arg2;
    }
}