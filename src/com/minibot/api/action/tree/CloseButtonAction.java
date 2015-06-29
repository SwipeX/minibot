package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class CloseButtonAction extends ButtonAction {

    public CloseButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_CLOSE, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_CLOSE;
    }
}