package com.minibot.client.natives;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 15:49
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface RSCharacter extends RSRenderableNode {
    int getX();
    int getY();
    int getAnimation();
    int getInteractingIndex();
    int getHealth();
    int getMaxHealth();
}
