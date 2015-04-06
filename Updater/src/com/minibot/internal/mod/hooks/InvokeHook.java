package com.minibot.internal.mod.hooks;

import com.minibot.internal.mod.Crypto;
import org.objectweb.asm.tree.MethodNode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public class InvokeHook extends Hook {

    public String clazz, method, desc;
    public int predicate = Integer.MAX_VALUE;
    public Class<?> predicateType = int.class;

    public InvokeHook(String name, String clazz, String method, String desc) {
        super(name);
        this.clazz = clazz;
        this.method = method;
        this.desc = desc;
    }

    public InvokeHook(String name, MethodNode mn) {
        this(name, mn.owner.name, mn.name, mn.desc);
    }

    public void setOpaquePredicate(int predicate, Class<?> predicateType) {
        this.predicate = predicate;
        this.predicateType = predicateType;
    }

    @Override
    public byte getType() {
        return Type.INVOKE;
    }

    @Override
    public String getOutput() {
        String out = "& " + name + " --> " + clazz + "." + method + desc;
        if (predicate != Integer.MAX_VALUE)
            out += " [" + predicate + "] - " + predicateType;
        return out;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(method);
        out.writeUTF(desc);
        out.writeInt(predicate);
        out.writeUTF(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S"));
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(method));
        out.writeUTF(Crypto.encrypt(desc));
        out.writeInt(predicate);
        out.writeUTF(Crypto.encrypt(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S")));
    }
}