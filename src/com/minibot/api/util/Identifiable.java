package com.minibot.api.util;

/**
 * Project: minibot
 * Date: 06-04-2015
 * Time: 23:39
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface Identifiable {

    public int id();

    public default String name() {
        throw new UnsupportedOperationException();
    }
}
