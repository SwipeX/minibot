package com.minibot.api.wrapper.node;


import com.minibot.api.wrapper.Wrapper;
import com.minibot.internal.mod.hooks.InvokeHook;
import com.minibot.internal.mod.hooks.ReflectionData;

/**
 * @author Tyler Sedlar
 */
@ReflectionData(className = "NodeDeque")
public class RSNodeDeque extends Wrapper {

    public RSNodeDeque(Object raw) {
        super(raw);
    }

    public Object head() {
        return hook("head").get(get());
    }

    public Object tail() {
        return hook("tail").get(get());
    }

    public Object current() {
        Object raw = get();
        if (raw == null)
            return null;
        try {
            InvokeHook ih = serveInvoke("current");
            if (ih != null)
                return ih.invoke(raw);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Object next() {
        Object raw = get();
        if (raw == null)
            return null;
        try {
            InvokeHook ih = serveInvoke("next");
            if (ih != null)
                return ih.invoke(raw);
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}