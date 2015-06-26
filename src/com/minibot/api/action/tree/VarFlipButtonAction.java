package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class VarFlipButtonAction extends ButtonAction {

    public VarFlipButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_VAR_FLIP, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_VAR_FLIP;
    }
}