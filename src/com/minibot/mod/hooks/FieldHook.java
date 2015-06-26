package com.minibot.mod.hooks;

import com.minibot.mod.Crypto;

import java.io.DataInputStream;
import java.io.IOException;

public class FieldHook extends Hook {

    private boolean isStatic;
    private int multiplier = -1;

    private String clazz;
    private String field;
    private String fieldDesc;

    @Override
    protected void readData(DataInputStream in) throws IOException {
        setName(Crypto.decrypt(in.readUTF()));
        clazz = Crypto.decrypt(in.readUTF());
        field = Crypto.decrypt(in.readUTF());
        fieldDesc = Crypto.decrypt(in.readUTF());
        isStatic = in.readBoolean();
        multiplier = in.readInt();
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