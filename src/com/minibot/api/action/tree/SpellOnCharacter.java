package com.minibot.api.action.tree;

public abstract class SpellOnCharacter extends SpellOnEntityAction {

    public SpellOnCharacter(int opcode, int entityId) {
        super(opcode, entityId, 0, 0);
    }

    @Override
    public final int significantArgs() {
        return ARG0;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0;
    }
}