package com.minibot.api.wrapper;

import com.minibot.internal.mod.hooks.Hookable;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public abstract class Wrapper implements Hookable {

    private Object raw;

    public Wrapper(Object raw) {
        this.raw = raw;
    }

    public void set(Object raw) {
        this.raw = raw;
    }

    public Object get() {
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
