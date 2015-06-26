package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class InputButtonAction extends ButtonAction {

    public InputButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_INPUT, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_INPUT;
    }
}