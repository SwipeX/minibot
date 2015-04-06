package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.UID;
import com.minibot.api.wrapper.def.ObjectDefinition;
import com.minibot.internal.def.DefinitionLoader;

public class ObjectAction extends EntityAction {

    public ObjectAction(int opcode, int uid, int localX, int localY) {
        super(opcode, uid, localX, localY);
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.OBJECT_ACTION_0;
    }

    public int entityId() { //Arg0 is the UID, not the direct id of the entity
        return objectId();
    }

    public int uid() {
        return arg0;
    }

    public int objectId() {
        return UID.entityId(uid());
    }

    public Object definition() {
        return DefinitionLoader.findObjectDefinition(entityId());
    }

    public String name() {
        return ObjectDefinition.name(definition());
    }

    public String actionName() {
        String[] actions = ObjectDefinition.actions(definition());
        if (actions == null)
            return null;
        int actionIndex = actionIndex();
        return actionIndex > 0 && actionIndex < actions.length ? actions[actionIndex] : null;
    }

    @Override
    public String toString() {
        return "Object Action [object-name(id=" + entityId() + ")=" + name() + ",action-name(index=" +
                actionIndex() + ")=" + actionName() + ")<" + x() + "," + y() + "> on object " /*+ getObject()*/;
    }
}
