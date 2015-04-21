package com.minibot.client.natives;

public interface RSNpcDefinition extends RSCacheableNode {

    String getName();
    String[] getActions();
    int getId();
    int getVarpIndex();
    int[] getTransformIds();

    RSNpcDefinition transform();
}
