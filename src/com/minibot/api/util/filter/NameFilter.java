package com.minibot.api.util.filter;

import com.minibot.api.util.Identifiable;

/**
 * Project: minibot
 * Date: 06-04-2015
 * Time: 23:45
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class NameFilter<I extends Identifiable> implements Filter<I> {

    private final String[] names;
    private final boolean contains;

    public NameFilter(final boolean contains, final String... names) {
        this.contains = contains;
        this.names = names;
    }

    @Override
    public boolean accept(I i) {
        for (final String name : names) {
            if (contains ? i.name().contains(name) : i.name().equals(name))
                return true;
        }
        return false;
    }
}
