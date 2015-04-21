package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.util.DefinitionLoader;

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

    public RSObjectDefinition definition() {
        return DefinitionLoader.findObjectDefinition(entityId());
    }

    public String name() {
        return definition().getName();
    }

    public String actionName() {
        String[] actions = definition().getActions();
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