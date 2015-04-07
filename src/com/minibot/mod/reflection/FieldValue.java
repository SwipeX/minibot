package com.minibot.mod.reflection;

import com.minibot.mod.ModScript;

import java.lang.reflect.Field;

/**
 * @author Tyler Sedlar
 */
public class FieldValue {

    private final RSClassLoader classloader;
    private final Field field;
    private final int multiplier;

    public FieldValue(RSClassLoader classloader, Field field, int multiplier) {
        this.classloader = classloader;
        if (field != null)
            field.setAccessible(true);
        this.field = field;
        this.multiplier = multiplier;
    }

    public Field field() {
        return field;
    }

    public boolean valid() {
        return field != null;
    }

    public void set(Object parent, Object value) {
        try {
            if (field() != null)
                field().set(parent, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(Object value) {
        set(null, value);
    }

    public Object get(Object parent) {
        try {
            Object o = field.get(parent);
            if (o != null && multiplier != -1)
                o = ((int) o) * multiplier;
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    public Object get() {
        return get((Object) null);
    }

    public Object get(String clazz, String field) {
        try {
            return classloader.load(clazz, field).get(get());
        } catch (Exception e) {
            return null;
        }
    }

    public Object get(String hook) {
        try {
            FieldValue value = ModScript.hook(hook);
            return value.get(get());
        } catch (Exception e) {
            return null;
        }
    }

    public int getInt(Object parent) {
        try {
            return (int) get(parent);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getInt() {
        return getInt((Object) null);
    }

    public int getInt(String clazz, String field) {
        try {
            return (int) get(clazz, field);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getInt(String hook) {
        try {
            return (int) get(hook);
        } catch (Exception e) {
            return -1;
        }
    }

    public short getShort(Object parent) {
        try {
            return (short) get(parent);
        } catch (Exception e) {
            return -1;
        }
    }

    public short getShort() {
        return getShort((Object) null);
    }

    public short getShort(String clazz, String field) {
        try {
            return (short) get(clazz, field);
        } catch (Exception e) {
            return -1;
        }
    }

    public short getShort(String hook) {
        try {
            return (short) get(hook);
        } catch (Exception e) {
            return -1;
        }
    }

    public long getLong(Object parent) {
        try {
            return (long) get(parent);
        } catch (Exception e) {
            return -1;
        }
    }

    public long getLong() {
        return getLong((Object) null);
    }

    public long getLong(String clazz, String field) {
        try {
            return (long) get(clazz, field);
        } catch (Exception e) {
            return -1;
        }
    }

    public long getLong(String hook) {
        try {
            return (long) get(hook);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getString(Object parent) {
        try {
            return (String) get(parent);
        } catch (Exception e) {
            return null;
        }
    }

    public String getString() {
        return getString((Object) null);
    }

    public String getString(String clazz, String field) {
        try {
            return (String) get(clazz, field);
        } catch (Exception e) {
            return null;
        }
    }

    public String getString(String hook) {
        try {
            return (String) get(hook);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getBoolean(Object parent) {
        try {
            return (boolean) get(parent);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBoolean() {
        return getBoolean((Object) null);
    }

    public boolean getBoolean(String clazz, String field) {
        try {
            return (boolean) get(clazz, field);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBoolean(String hook) {
        try {
            return (boolean) get(hook);
        } catch (Exception e) {
            return false;
        }
    }
}
