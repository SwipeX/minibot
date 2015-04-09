package com.minibot.client.natives;

import com.minibot.client.Artificial;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 19:10
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
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
