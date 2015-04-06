package com.minibot.internal.mod.hooks;

import com.minibot.internal.mod.Crypto;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public class FieldHook extends Hook {

    public String clazz, field, fieldDesc;
    public boolean isStatic;

    public int multiplier = -1;

    @Override
    protected void readData(DataInputStream in) throws IOException {
        name = Crypto.crypt(in.readUTF());
        clazz = Crypto.crypt(in.readUTF());
        field = Crypto.crypt(in.readUTF());
        fieldDesc = Crypto.crypt(in.readUTF());
        isStatic = in.readBoolean();
        multiplier = in.readInt();
    }
}
