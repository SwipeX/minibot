package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;

// We can extend out respected action type since we are not a entity, and allow for more simplicity
public class SpellOnItemAction extends AbstractTableAction {

    public SpellOnItemAction(int itemId, int itemIndex, int tableId) {
        super(ActionOpcodes.SPELL_ON_ITEM, itemId, itemIndex, tableId);
    }

    public static boolean isInstance(int opcode) {
        return Action.pruneOpcode(opcode) == ActionOpcodes.SPELL_ON_ITEM;
    }
}