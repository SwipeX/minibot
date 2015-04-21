package com.minibot.client.natives;

public interface RSRenderableNode extends RSCacheableNode {
    boolean isDrawingDisabled();
    void setDrawingDisabled(boolean disabled);
}
