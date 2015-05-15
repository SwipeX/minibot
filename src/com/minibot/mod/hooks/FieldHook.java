package com.minibot.mod.hooks;

import com.minibot.mod.Crypto;

import java.io.DataInputStream;
import java.io.IOException;

public class FieldHook extends Hook {

    public String clazz, field, fieldDesc;
    public boolean isStatic;

    public int multiplier = -1;

    @Override
    protected void readData(DataInputStream in) throws IOException {
        name = Crypto.decrypt(in.readUTF());
        clazz = Crypto.decrypt(in.readUTF());
        field = Crypto.decrypt(in.readUTF());
        fieldDesc = Crypto.decrypt(in.readUTF());
        isStatic = in.readBoolean();
        multiplier = in.readInt();
    }
}
