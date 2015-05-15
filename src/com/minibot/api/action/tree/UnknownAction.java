package com.minibot.api.action.tree;

public class UnknownAction extends Action {

    public UnknownAction(int opcode, int arg0, int arg1, int arg2) {
        super(opcode, arg0, arg1, arg2);
    }

    @Override
    public int significantArgs() {
        return SIG_ALL;
    }
}