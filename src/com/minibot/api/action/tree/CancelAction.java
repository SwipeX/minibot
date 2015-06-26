package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class CancelAction extends NotifyingAction {

    public CancelAction() {
        super(ActionOpcodes.CANCEL);
    }

    public static boolean isInstance(int opcode) {
        return opcode == ActionOpcodes.CANCEL;
    }
}