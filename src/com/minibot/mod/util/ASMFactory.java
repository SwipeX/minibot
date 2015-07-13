package com.minibot.mod.util;

import com.minibot.mod.hooks.FieldHook;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

/**
 * yeeeeeeeeee
 * put all the nigs here
 */
public final class ASMFactory implements Opcodes {

    private ASMFactory() {
    }

    /**
     * pass custom desc because known array descs e.g: getX[] -> Player[]
     */
    public static MethodNode createGetter(FieldHook hook, String returnDesc) {
        MethodNode dankMeth = new MethodNode(ACC_PUBLIC, methodifyFieldName(hook.getName(), hook.getFieldDesc()),
                "()" + returnDesc, null, null);
        if (!hook.isStatic()) {
            dankMeth.instructions.add(new VarInsnNode(ALOAD, 0));
        }
        dankMeth.instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getClazz(), hook.getField(), hook.getFieldDesc()));
        /**
         * TODO set default multiplier to 0
         * reason being is because the multiplier can never legit be 0, but it can actually be -1
         */
        if (hook.getMultiplier() != -1) {
            dankMeth.instructions.add(new LdcInsnNode(hook.getMultiplier()));
            dankMeth.instructions.add(new InsnNode(IMUL));
        }
        dankMeth.instructions.add(new InsnNode(getReturnOpcode(hook.getFieldDesc())));
        return dankMeth;
    }

    public static MethodNode createGetter(FieldHook hook) {
        return createGetter(hook, hook.getFieldDesc());
    }

    public static MethodNode createGetter(boolean stadik, String owner, String name, String desc, String definedFieldName) {
        FieldHook hook = new FieldHook();
        hook.setName(definedFieldName);
        hook.setFieldDesc(desc);
        hook.setField(name);
        hook.setClazz(owner);
        hook.setIsStatic(stadik);
        return createGetter(hook);
    }

    private static String methodifyFieldName(String name, String desc) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
        return desc.equals("Z") ? "is" + name : "get" + name;
    }

    public static int getReturnOpcode(String desc) {
        desc = desc.substring(desc.indexOf(")") + 1);
        if (desc.length() > 1) {
            return ARETURN;
        }
        char c = desc.charAt(0);
        switch (c) {
            case 'I':
            case 'Z':
            case 'B':
            case 'S':
            case 'C':
                return IRETURN;
            case 'J':
                return LRETURN;
            case 'F':
                return FRETURN;
            case 'D':
                return DRETURN;
            case 'V':
                return RETURN;
            default: {
                throw new RuntimeException("bad_return@" + desc);
            }
        }
    }
}