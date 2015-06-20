package com.minibot.client.natives;

public interface RSRenderableNode extends RSCacheableNode {
    int getHeight();
    boolean isDrawingDisabled();
    void setDrawingDisabled(boolean disabled);
}
