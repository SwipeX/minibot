package com.minibot.client.natives;

/**
 * @author Tyler Sedlar
 * @since 7/22/2015
 */
public interface RSProjectile extends RSRenderableNode {

    RSAnimationSequence getSequence();

    boolean isMoving();

    int getId();
    int getCycle();
}
