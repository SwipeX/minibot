package com.minibot.api.wrapper;

import com.minibot.client.natives.ClientNative;
import com.minibot.mod.hooks.Hookable;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public abstract class Wrapper<T extends ClientNative> implements Hookable {

    protected T raw;

    public Wrapper(T raw) {
        if (raw == null)
            throw new IllegalArgumentException("raw == null");
        this.raw = raw;
    }

    public void set(T raw) {
        if (raw == null)
            throw new IllegalArgumentException("raw == null");
        this.raw = raw;
    }

    public T get() {
        return raw;
    }

    public boolean valid() {
        return raw != null;
    }

    @Override
    public int hashCode() {
        return valid() ? raw.hashCode() : super.hashCode();
    }
}
