package com.minibot.client.natives;

public interface RSObjectDefinition extends RSCacheableNode {

    String getName();
    int getId();
    int getVarpIndex();
    int[] getTransformIds();
    String[] getActions();

    RSObjectDefinition transform();
}
