package com.minibot.api.action.tree;

public interface ActionFilter {

    boolean accept(int opcode, int arg0, int arg1, int arg2);
}