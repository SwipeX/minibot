package com.minibot.mod.hooks;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public abstract class Hook {

    protected class Type {

        public static final byte FIELD = 0;
        public static final byte INVOKE = 1;
    }

    public final String name;

    public Hook(String name) {
        this.name = name;
    }

    public abstract byte getType();

    public abstract String getOutput();

    protected abstract void writeData(DataOutputStream out) throws IOException;

    protected abstract void writeEncryptedData(DataOutputStream out) throws IOException;

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeByte(getType());
        writeData(out);
    }

    public void writeToEncryptedStream(DataOutputStream out) throws IOException {
        out.writeByte(getType());
        writeEncryptedData(out);
    }
}