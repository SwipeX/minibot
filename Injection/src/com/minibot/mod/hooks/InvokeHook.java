package com.minibot.mod.hooks;

import com.minibot.mod.Crypto;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public class InvokeHook extends Hook {

    public String clazz, method, desc;
    public int predicate;
    public String predicateType;
    public Class<?> predicateTypeClass;

    @Override
    protected void readData(DataInputStream in) throws IOException {
        name = Crypto.crypt(in.readUTF());
        clazz = Crypto.crypt(in.readUTF());
        method = Crypto.crypt(in.readUTF());
        desc = Crypto.crypt(in.readUTF());
        predicate = in.readInt();
        predicateType = Crypto.crypt(in.readUTF());
    }
}