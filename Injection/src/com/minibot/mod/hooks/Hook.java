package com.minibot.mod.hooks;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public abstract class Hook {

    public String name;

    private enum Type_ {

        FIELD(Type.FIELD, FieldHook.class),
        INVOKE(Type.INVOKE, InvokeHook.class);

        private final int id;
        private final Class<? extends Hook> clazz;

        private Type_(int id, Class<? extends Hook> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public static Class<? extends Hook> forID(int id) {
            for (Type_ t : values()) {
                if (t.id == id)
                    return t.clazz;
            }
            return null;
        }
    }

    private interface Type {

        public static final byte FIELD = 0;
        public static final byte INVOKE = 1;
    }

    protected abstract void readData(DataInputStream in) throws IOException;

    public static Hook readDataStream(DataInputStream in) throws IOException {
        int type = in.readByte();
        Class<? extends Hook> clazz = Type_.forID(type);
        Hook hook;
        try {
            hook = clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
        hook.readData(in);
        return hook;
    }
}