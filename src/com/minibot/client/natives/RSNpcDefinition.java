package com.minibot.client.natives;

public interface RSNpcDefinition extends RSCacheableNode {

    int getId();
    int getVarpIndex();
    int[] getTransformIds();

    String getName();
    String[] getActions();
    RSNpcDefinition transform();
}