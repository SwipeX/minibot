package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class DialogButtonAction extends ButtonAction {

    public DialogButtonAction(int widgetUid) {
        super(ActionOpcodes.BUTTON_DIALOG, widgetUid);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_DIALOG;
    }
}
