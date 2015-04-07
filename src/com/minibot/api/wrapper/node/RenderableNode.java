package com.minibot.api.wrapper.node;

import com.minibot.api.wrapper.Wrapper;
import com.minibot.mod.hooks.ReflectionData;

/**
 * @author Tyler Sedlar
 * @since 2/14/2015
 */
@ReflectionData(className = "RenderableNode")
public class RenderableNode extends Wrapper {

    public RenderableNode(Object raw) {
        super(raw);
    }

    public int height() {
        return hook("height").getInt(get());
    }
}
