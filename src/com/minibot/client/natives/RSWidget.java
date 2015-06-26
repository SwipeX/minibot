package com.minibot.client.natives;

public interface RSWidget extends ClientNative {

    int getId();
    int getOwnerId();
    int getBoundsIndex();
    int getItemId();
    int getItemAmount();
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    int getScrollX();
    int getScrollY();
    int getType();
    int getIndex();
    int[] getItemIds();
    int[] getStackSizes();
    int getTextureId();
    boolean isHidden();

    RSWidget[] getChildren();
    String[] getActions();
    String getText();
}