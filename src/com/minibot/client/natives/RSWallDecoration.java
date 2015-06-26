package com.minibot.client.natives;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
public interface RSWallDecoration extends RSDecoration {

    int getOrientation();

    RSRenderableNode getBackup();
}