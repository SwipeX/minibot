package com.minibot.api.action.tree;

// Though would be more explicit if it extends its
// type entity (eg. SpellOnNpc extends NpcAction extends ItemOnEntity),
// we can not extend multiple classes

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.wrapper.EntityType;

public class ItemOnEntityAction extends EntityAction {

    public ItemOnEntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public static boolean isInstance(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.ITEM_ON_OBJECT:
            case ActionOpcodes.ITEM_ON_NPC:
            case ActionOpcodes.ITEM_ON_PLAYER:
            case ActionOpcodes.ITEM_ON_GROUND_ITEM: {
                return true;
            }
        }
        return false;
    }

    //Keeps within the scope of itemOn opcodes, when asserting an instance of itemOn
    public static EntityType itemOp2EntityType(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.ITEM_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.ITEM_ON_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.ITEM_ON_PLAYER: {
                return EntityType.PLAYER;
            }
            case ActionOpcodes.ITEM_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }
}