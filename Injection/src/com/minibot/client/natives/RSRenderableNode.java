package com.minibot.client.natives;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 15:48
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface RSRenderableNode extends RSCacheableNode {
    boolean isDrawingDisabled();
    void setDrawingDisabled(boolean disabled);
}
