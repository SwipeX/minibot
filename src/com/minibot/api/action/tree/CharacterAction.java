package com.minibot.api.action.tree;

//Character based action do not hold any geographic information, because the location is dynamic,
//though normally the default is 0,0
public abstract class CharacterAction extends EntityAction {

    public CharacterAction(int opcode, int entityId) {
        super(opcode, entityId, 0, 0);
    }

    @Override
    public final int significantArgs() {
        return ARG0;
    }

    //Compares opcode and arg0
    @Override
    public final boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0;
    }
}