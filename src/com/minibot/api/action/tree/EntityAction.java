package com.minibot.api.action.tree;

import com.minibot.api.method.Game;
import com.minibot.api.wrapper.EntityType;

import static com.minibot.api.action.ActionOpcodes.*;

// For all entity types
public abstract class EntityAction extends Action {

    protected final EntityType type;

    public EntityAction(int opcode, int entityId, int localX, int localY) {
        super(opcode, entityId, localX, localY);
        this.type = op2EntityType(opcode); // Must match with the respected opcode
    }

    public int significantArgs() {
        return SIG_ALL;
    }

    // The type of entity this action is targeting
    public final EntityType entityType() {
        return type;
    }

    // Not all -general- types have multiple actions, like spellOnEntity, or ItemOnEntity
    public int actionIndex() {
        return -1;
    }

    public int entityId() {
        return arg0;
    }

    // The regional position of the entity when this action was created, can not be guaranteed to be real time
    public int localX() {
        return arg1;
    }

    public int localY() {
        return arg2;
    }

    public int x() {
        return Game.baseX() + localX();
    }

    public int y() {
        return Game.baseY() + localY();
    }

    // Determines the entity type the action targets or is directly references by its opcode
    public static EntityType op2EntityType(int opcode) {
        switch (Action.pruneOpcode(opcode)) {
            case OBJECT_ACTION_0:
            case OBJECT_ACTION_1:
            case OBJECT_ACTION_2:
            case OBJECT_ACTION_3:
            case OBJECT_ACTION_4:
            case EXAMINE_OBJECT:
            case ITEM_ON_OBJECT:
            case SPELL_ON_OBJECT: {
                return EntityType.OBJECT;
            }
            case GROUND_ITEM_ACTION_0:
            case GROUND_ITEM_ACTION_1:
            case GROUND_ITEM_ACTION_2:
            case GROUND_ITEM_ACTION_3:
            case GROUND_ITEM_ACTION_4:
            case EXAMINE_GROUND_ITEM:
            case ITEM_ON_GROUND_ITEM:
            case SPELL_ON_GROUND_ITEM: {
                return EntityType.GROUND_ITEM;
            }
            case NPC_ACTION_0:
            case NPC_ACTION_1:
            case NPC_ACTION_2:
            case NPC_ACTION_3:
            case NPC_ACTION_4:
            case EXAMINE_NPC:
            case ITEM_ON_NPC:
            case SPELL_ON_NPC: {
                return EntityType.NPC;
            }
            case PLAYER_ACTION_0:
            case PLAYER_ACTION_1:
            case PLAYER_ACTION_2:
            case PLAYER_ACTION_3:
            case PLAYER_ACTION_4:
            case PLAYER_ACTION_5:
            case PLAYER_ACTION_6:
            case PLAYER_ACTION_7:
            case ITEM_ON_PLAYER:
            case SPELL_ON_PLAYER: {
                return EntityType.PLAYER;
            }
        }
        return null;

    }

    @Override
    public boolean valid() {
        if (!super.valid())
            return false;
        int x = localX(), y = localY();
        return x >= 0 && x <= 104 && y >= 0 && y <= 104;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0 && this.arg1 == arg1 && this.arg2 == arg2;
    }
}
