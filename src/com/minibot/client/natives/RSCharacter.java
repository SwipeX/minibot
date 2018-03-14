package com.minibot.client.natives;

public interface RSCharacter extends RSRenderableNode {

    int getX();

    int getY();

    int getAnimation();

    int getInteractingIndex();

    int getOrienatation();

    RSLinkedList getHealthBars();
}