package com.minibot.client.natives;

public interface RSHitUpdate extends ClientNative {

    int getStartCycle();
    int getStartWidth();

    int getCurrentCycle();
    int getCurrentWidth();
}
