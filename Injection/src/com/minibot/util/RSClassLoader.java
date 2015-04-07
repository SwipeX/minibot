package com.minibot.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: minibot
 * Date: 07-04-2015
 * Time: 00:58
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class RSClassLoader extends ClassLoader {

    public final Map<String, byte[]> classes;

    public final Map<String, Class<?>> loaded = new HashMap<>();
    public final Map<String, Class<?>> defined = new HashMap<>();

    public RSClassLoader(final Map<String, byte[]> classes) {
        this.classes = classes;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (loaded.containsKey(name))
            return loaded.get(name);
        if (!classes.containsKey(name))
            return super.loadClass(name);
        if (defined.containsKey(name))
            return defined.get(name);
        final byte[] def = classes.get(name);
        final Class<?> clazz = super.defineClass(name, def, 0, def.length);
        loaded.put(name, clazz);
        defined.put(name, clazz);
        return clazz;
    }
}
