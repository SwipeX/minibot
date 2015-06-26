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

    private final Map<String, byte[]> classes;

    private final Map<String, Class<?>> loaded = new HashMap<>();
    private final Map<String, Class<?>> defined = new HashMap<>();

    public RSClassLoader(Map<String, byte[]> classes) {
        this.classes = classes;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (loaded.containsKey(name))
            return loaded.get(name);
        if (!classes.containsKey(name))
            return super.loadClass(name);
        if (defined.containsKey(name))
            return defined.get(name);
        byte[] def = classes.get(name);
        Class<?> clazz = defineClass(name, def, 0, def.length);
        loaded.put(name, clazz);
        defined.put(name, clazz);
        return clazz;
    }

    public Map<String, byte[]> getClasses() {
        return classes;
    }

    public Map<String, Class<?>> getLoaded() {
        return loaded;
    }

    public Map<String, Class<?>> getDefined() {
        return defined;
    }
}