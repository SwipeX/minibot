package com.minibot.client.natives;

public interface RSTile extends ClientNative {

    RSInteractableObject[] getObjects();
    int getPlane();
    int getX();
    int getY();
}
