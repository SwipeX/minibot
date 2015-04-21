package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.wrapper.EntityType;

public class ExamineEntityAction extends EntityAction {

    public ExamineEntityAction(int opcode, int entity_id, int local_x, int local_y) {
        super(opcode, entity_id, local_x, local_y);
    }

    // Keeps within the scope of examine opcodes
    public static EntityType examineOp2EntityType(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.EXAMINE_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.EXAMINE_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.EXAMINE_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }
}
