package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class WalkHereAction extends NotifyingAction {

    public WalkHereAction() {
        super(ActionOpcodes.WALK_HERE);
    }

    public int screenX() {
        return arg1;
    }

    public int screenY() {
        return arg2;
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.WALK_HERE;
    }
}
