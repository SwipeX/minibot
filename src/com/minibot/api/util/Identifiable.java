package com.minibot.api.util;

public interface Identifiable {

    /**
     * @return The ID assigned to this {@link Identifiable}
     */
    public int id();

    /**
     * Throws an {@link UnsupportedOperationException} if this {@link Identifiable}
     * has no name
     * @return The name assigned to this {@link Identifiable}
     */
    public default String name() {
        throw new UnsupportedOperationException();
    }
}