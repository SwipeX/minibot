package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class TableAction extends AbstractTableAction {

    public TableAction(int opcode, int item_id, int item_index, int containerUID) {
        super(opcode, item_id, item_index, containerUID);
    }

    public static int actionIndexOpcode(int index) {
        return index < 0 || index > 4 ? -1 : ActionOpcodes.TABLE_ACTION_0 + index;
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.TABLE_ACTION_0;
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode >= ActionOpcodes.TABLE_ACTION_0 && opcode <= ActionOpcodes.TABLE_ACTION_4;
    }
}
