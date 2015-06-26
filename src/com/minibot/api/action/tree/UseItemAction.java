package com.minibot.api.action.tree;


import com.minibot.api.action.ActionOpcodes;

public class UseItemAction extends AbstractTableAction {

    public UseItemAction(int itemId, int tableIndex, int tableId) {
        super(ActionOpcodes.USE_ITEM, itemId, tableIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.USE_ITEM;
    }
}