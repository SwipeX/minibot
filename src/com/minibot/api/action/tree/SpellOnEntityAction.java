package com.minibot.api.action.tree;

// Though would be more explicit if it extends its
// type entity (eg. SpellOnNpc extends NpcAction extends SpellAction),
// we can not extend multiple classes

// ^ It's more important that its marker as a entity action,
// with the entity type known through EntityAction.entityType

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.wrapper.EntityType;

public class SpellOnEntityAction extends EntityAction {

    public SpellOnEntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
    }

    public static EntityType spellOp2EntityType(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case ActionOpcodes.SPELL_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case ActionOpcodes.SPELL_ON_NPC: {
                return EntityType.NPC;
            }
            case ActionOpcodes.SPELL_ON_PLAYER: {
                return EntityType.PLAYER;
            }
            case ActionOpcodes.SPELL_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
        }
        return null;
    }

    @Override
    public boolean valid() {
        return super.valid() && type != null;
    }
}