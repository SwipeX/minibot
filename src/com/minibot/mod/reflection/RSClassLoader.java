package com.minibot.mod.reflection;

import com.minibot.mod.ModScript;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 */
public class RSClassLoader {

    private final ClassLoader classloader;

    private final Map<String, FieldValue> fields = new HashMap<>();

    public RSClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    public Class<?> loadClass(String clazz) {
        try {
            return classloader.loadClass(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public FieldValue load(String clazz, String field, int multiplier) {
        String key = clazz + "." + field;
        if (fields.containsKey(key) && fields.get(key) != null)
            return fields.get(key);
        try {
            Field f = loadClass(clazz).getDeclaredField(field);
            if (f != null)
                return fields.put(key, new FieldValue(this, f, multiplier));
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }

    public FieldValue load(String clazz, String field) {
        return load(clazz, field, -1);
    }

    public FieldValue load(String hook) {
        return ModScript.hook(hook);
    }
}
