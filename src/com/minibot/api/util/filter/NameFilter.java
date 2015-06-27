package com.minibot.api.util.filter;

import com.minibot.api.util.Identifiable;

public class NameFilter<I extends Identifiable> implements Filter<I> {

    private final String[] names;
    private final boolean contains;

    public NameFilter(boolean contains, String... names) {
        this.contains = contains;
        this.names = names;
    }

    @Override
    public boolean accept(I i) {
        for (String name : names) {
            if (contains ? i.name().contains(name) : i.name().equals(name))
                return true;
        }
        return false;
    }
}