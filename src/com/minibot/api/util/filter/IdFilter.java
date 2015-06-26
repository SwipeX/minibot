package com.minibot.api.util.filter;

import com.minibot.api.util.Identifiable;

public class IdFilter<I extends Identifiable> implements Filter<I> {

    private final int[] ids;

    public IdFilter(int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean accept(I i) {
        for (int id : ids) {
            if (id == i.id())
                return true;
        }
        return false;
    }
}