package com.minibot.util;

import java.util.Iterator;

/**
 * @author Tyler Sedlar
 */
public class ArrayIterator<E> implements Iterator<E> {

    private final E[] elements;

    private int index;

    @SafeVarargs
    public ArrayIterator(E... elements) {
        this.elements = elements;
    }

    public int size() {
        return elements.length;
    }

    public E get(int index) {
        return elements[index];
    }

    @Override
    public boolean hasNext() {
        return index < elements.length;
    }

    @Override
    public E next() {
        return hasNext() ? elements[index++] : null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Element removal not supported by ArrayIterator");
    }
}