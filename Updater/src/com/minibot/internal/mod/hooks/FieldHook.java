package com.minibot.internal.mod.hooks;

import com.minibot.internal.mod.Crypto;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Tyler Sedlar
 */
public class FieldHook extends Hook {

    public String clazz;
    public String field;
    public String fieldDesc;
    public boolean isStatic;

    public int multiplier = -1;

    public FieldHook(String name, String clazz, String field, String fieldDesc, boolean isStatic) {
        super(name);
        this.clazz = clazz;
        this.field = field;
        this.fieldDesc = fieldDesc;
        this.isStatic = isStatic;
    }

    public FieldHook(String name, String clazz, String field, String fieldDesc) {
        this(name, clazz, field, fieldDesc, false);
    }

    public FieldHook(String name, FieldInsnNode fin) {
        this(name, fin.owner, fin.name, fin.desc, fin.opcode() == GETSTATIC || fin.opcode() == PUTSTATIC);
    }

    public FieldHook(String name, FieldNode fn) {
        this(name, fn.owner.name, fn.name, fn.desc, (fn.access & ACC_STATIC) > 0);
    }

    @Override
    public byte getType() {
        return Hook.Type.FIELD;
    }

    @Override
    public String getOutput() {
        StringBuilder output = new StringBuilder();
        output.append("# ").append(name).append(" --> ").append(clazz).append('.').append(field);
        if (multiplier != -1)
            output.append(" * ").append(multiplier);
        output.append(" - ");
        if (isStatic)
            output.append("static ");
        output.append(fieldDesc);
        return output.toString();
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(field);
        out.writeUTF(fieldDesc);
        out.writeBoolean(isStatic);
        out.writeInt(multiplier);
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(field));
        out.writeUTF(Crypto.encrypt(fieldDesc));
        out.writeBoolean(isStatic);
        out.writeInt(multiplier);
    }
}
