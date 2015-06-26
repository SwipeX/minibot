package com.minibot.util;

/**
 * @author Tyler Sedlar
 * @since 3/1/2015
 */
public interface Filter<E> {

    boolean accept(E e);
}