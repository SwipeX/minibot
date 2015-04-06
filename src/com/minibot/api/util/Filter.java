package com.minibot.api.util;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public interface Filter<E> {

    public boolean accept(E e);
}
