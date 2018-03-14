package com.minibot.mod.hooks;

import com.minibot.mod.Crypto;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.PUTSTATIC;

/**
 * @author Tyler Sedlar
 */
public class FieldHook extends Hook {

    private String clazz;
    private String field;
    private String fieldDesc;
    private boolean isStatic;

    private int multiplier = -1;

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

    public static FieldHook raw(String name, String desc) {
        return new FieldHook(name, null, null, desc);
    }

    @Override
    public byte getType() {
        return Hook.Type.FIELD;
    }

    @Override
    public String getOutput() {
        StringBuilder output = new StringBuilder();
        output.append("# ").append(name).append(" --> ").append(clazz).append('.').append(field);
        if (multiplier != -1) {
            output.append(" * ").append(multiplier);
        }
        output.append(" - ");
        if (isStatic) {
            output.append("static ");
        }
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

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}