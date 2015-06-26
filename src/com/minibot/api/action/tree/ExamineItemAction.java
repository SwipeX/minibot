package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class ExamineItemAction extends AbstractTableAction {

    public ExamineItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.EXAMINE_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.EXAMINE_ITEM;
    }
}