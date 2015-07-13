package com.minibot.mod.hooks;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public abstract class Hook {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private enum Type {

        FIELD(ID.FIELD, FieldHook.class),
        INVOKE(ID.INVOKE, InvokeHook.class);

        private final int id;
        private final Class<? extends Hook> clazz;

        Type(int id, Class<? extends Hook> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public static Class<? extends Hook> forID(int id) {
            for (Type t : values()) {
                if (t.id == id) {
                    return t.clazz;
                }
            }
            return null;
        }
    }

    private interface ID {

        byte FIELD = 0;
        byte INVOKE = 1;
    }

    protected abstract void readData(DataInputStream in) throws IOException;

    public static Hook readDataStream(DataInputStream in) throws IOException {
        int type = in.readByte();
        Class<? extends Hook> clazz = Type.forID(type);
        Hook hook;
        try {
            assert clazz != null;
            hook = clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
        hook.readData(in);
        return hook;
    }
}