package com.minibot.api.wrapper;

import com.minibot.client.natives.ClientNative;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public abstract class Wrapper<T extends ClientNative> {

    protected final T raw;

    public Wrapper(T raw) {
        if (raw == null) {
            throw new IllegalArgumentException("raw == null");
        }
        this.raw = raw;
    }

    public T raw() {
        return raw;
    }

    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public abstract void processAction(String action);

    public abstract void processAction(int opcode, String action);
}
