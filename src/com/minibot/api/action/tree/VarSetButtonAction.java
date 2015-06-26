package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class VarSetButtonAction extends ButtonAction {

    public VarSetButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_VAR_SET, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_VAR_SET;
    }
}