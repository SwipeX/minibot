package com.minibot.client.natives;

public interface RSObjectDefinition extends RSCacheableNode {

    int getId();
    int getVarpIndex();
    int[] getTransformIds();

    String getName();
    String[] getActions();
    RSObjectDefinition transform();
}