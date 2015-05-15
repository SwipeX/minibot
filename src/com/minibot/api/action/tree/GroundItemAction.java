package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class GroundItemAction extends EntityAction {

    public GroundItemAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public int itemId() {
        return entityId();
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.GROUND_ITEM_ACTION_0;
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode >= ActionOpcodes.GROUND_ITEM_ACTION_0 && opcode <= ActionOpcodes.GROUND_ITEM_ACTION_4;
    }
}
