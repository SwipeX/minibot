package com.minibot.api.util;

public interface Identifiable {

    /**
     * @return The ID assigned to this {@link com.minibot.api.util.Identifiable}
     */
    public int id();

    /**
     * Throws an {@link java.lang.UnsupportedOperationException} if this {@link com.minibot.api.util.Identifiable}
     * has no name
     * @return The name assigned to this {@link com.minibot.api.util.Identifiable}
     */
    public default String name() {
        throw new UnsupportedOperationException();
    }
}
