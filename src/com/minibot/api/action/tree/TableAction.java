package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class TableAction extends AbstractTableAction {

    public TableAction(int opcode, int itemId, int itemIndex, int containerUid) {
        super(opcode, itemId, itemIndex, containerUid);
    }

    public static int actionIndexOpcode(int index) {
        return index < 0 || index > 4 ? -1 : ActionOpcodes.TABLE_ACTION_0 + index;
    }

    @Override
    public int actionIndex() {
        return opcode - ActionOpcodes.TABLE_ACTION_0;
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode >= ActionOpcodes.TABLE_ACTION_0 && opcode <= ActionOpcodes.TABLE_ACTION_4;
    }
}