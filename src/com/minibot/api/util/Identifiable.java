package com.minibot.api.util;

public interface Identifiable {

    /**
     * @return The ID assigned to this {@link Identifiable}
     */
    int id();

    /**
     * Throws an {@link UnsupportedOperationException} if this {@link Identifiable}
     * has no name
     *
     * @return The name assigned to this {@link Identifiable}
     */
    default String name() {
        throw new UnsupportedOperationException();
    }
}