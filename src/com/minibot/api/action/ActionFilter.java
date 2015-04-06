package com.minibot.api.action;

public interface ActionFilter {

    public boolean accept(int opcode, int arg0, int arg1, int arg2);
}
