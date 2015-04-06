package com.minibot.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Populous<E> extends LinkedList<E> {

    private final HashMap<E, Integer> counts = new HashMap<>();

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (!counts.containsKey(element))
            counts.put(element, 0);
        counts.put(element, counts.get(element) + 1);
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        for (E element : elements) {
            if (!add(element))
                return false;
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        counts.remove(o);
        return removed;
    }

    @Override
    public void clear() {
        super.clear();
        counts.clear();
    }

    public int population(E element) {
        return counts.get(element);
    }

    public int uniqueCount() {
        return counts.size();
    }

    public E top() {
        E element = null;
        int count = 0;
        for (Map.Entry<E, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > count) {
                element = entry.getKey();
                count = entry.getValue();
            }
        }
        return element;
    }

    public E bottom() {
        E element = null;
        int count = Integer.MAX_VALUE;
        for (Map.Entry<E, Integer> entry : counts.entrySet()) {
            if (entry.getValue() < count) {
                element = entry.getKey();
                count = entry.getValue();
            }
        }
        return element;
    }
}