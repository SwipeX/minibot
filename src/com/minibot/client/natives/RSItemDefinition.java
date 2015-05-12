package com.minibot.client.natives;

public interface RSItemDefinition extends RSCacheableNode {
    String getName();
    int getId();
    String[] getActions();
    String[] getGroundActions();
}
