package com.minibot.client.natives;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
public interface RSDecoration extends ClientNative {

    int getWorldX();
    int getWorldY();
    int getPlane();
    int getId();
    int getFlags();
    RSRenderableNode getModel();
}
