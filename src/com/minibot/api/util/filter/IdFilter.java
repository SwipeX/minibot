package com.minibot.api.util.filter;

import com.minibot.api.util.Identifiable;

/**
 * Project: minibot
 * Date: 06-04-2015
 * Time: 23:44
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class IdFilter<I extends Identifiable> implements Filter<I> {

    private final int[] ids;

    public IdFilter(final int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean accept(I i) {
        for (final int id : ids) {
            if (id == i.id())
                return true;
        }
        return false;
    }
}
