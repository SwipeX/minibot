package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

public class ItemOnItemAction extends AbstractTableAction {

    public ItemOnItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.ITEM_ON_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.ITEM_ON_ITEM;
    }
}
