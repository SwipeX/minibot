package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class DialogButtonAction extends ButtonAction {

    public DialogButtonAction(int widgetUid, int buttonIndex) {
        super(ActionOpcodes.BUTTON_DIALOG, buttonIndex, widgetUid);
    }

    public DialogButtonAction(int widgetUid) {
        this(widgetUid, 0);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.BUTTON_DIALOG;
    }
}