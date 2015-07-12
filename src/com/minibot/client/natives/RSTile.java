package com.minibot.client.natives;

public interface RSTile extends ClientNative {

    int getPlane();

    int getX();

    int getY();

    RSInteractableObject[] getObjects();

    RSWallDecoration getWallDecoration();

    RSFloorDecoration getFloorDecoration();

    RSBoundary getBoundary();
}