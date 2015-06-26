package com.minibot.client.natives;

public interface RSItemDefinition extends RSCacheableNode {

    int getId();

    String getName();
    String[] getActions();
    String[] getGroundActions();
}