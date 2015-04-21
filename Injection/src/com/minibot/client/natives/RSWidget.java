package com.minibot.client.natives;

import com.minibot.client.Artificial;

public interface RSWidget extends ClientNative {
    RSWidget[] getChildren();
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
    String[] getActions();
    String getText();
    boolean isHidden();

    @Artificial
    int getContainerX();

    @Artificial
    int getContainerY();
}
