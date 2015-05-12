package com.minibot.mod.util;

import com.minibot.mod.hooks.FieldHook;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

/**
 * yeeeeeeeeee
 * put all the nigs here
 */
public final class ASMFactory implements Opcodes {

    private ASMFactory() {}

    /**
     * pass custom desc because known array descs e.g: getX[] -> Player[]
     */
    public static MethodNode createGetter(FieldHook hook, String returnDesc) {
        MethodNode dankMeth = new MethodNode(ACC_PUBLIC, methodifyFieldName(hook.name, hook.fieldDesc),
                "()" + returnDesc, null, null);
        if (!hook.isStatic)
            dankMeth.instructions.add(new VarInsnNode(ALOAD, 0));
        dankMeth.instructions.add(new FieldInsnNode(hook.isStatic ? GETSTATIC : GETFIELD, hook.clazz, hook.field, hook.fieldDesc));
        /**
         * TODO set default multiplier to 0
         * reason being is because the multiplier can never legit be 0, but it can actually be -1
         */
        if (hook.multiplier != -1) {
            dankMeth.instructions.add(new LdcInsnNode(hook.multiplier));
            dankMeth.instructions.add(new InsnNode(IMUL));
        }
        dankMeth.instructions.add(new InsnNode(getReturnOpcode(hook.fieldDesc)));
        return dankMeth;
    }

    public static MethodNode createGetter(FieldHook hook) {
        return createGetter(hook, hook.fieldDesc);
    }

    public static MethodNode createGetter(boolean stadik, String owner, String name, String desc, String definedFieldName) {
        FieldHook hook = new FieldHook();
        hook.name = definedFieldName;
        hook.fieldDesc = desc;
        hook.field = name;
        hook.clazz = owner;
        hook.isStatic = stadik;
        return createGetter(hook);
    }

    private static String methodifyFieldName(String name, String desc) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
        return desc.equals("Z") ? "is" + name : "get" + name;
    }

    public static int getReturnOpcode(String desc) {
        desc = desc.substring(desc.indexOf(")") + 1);
        if (desc.length() > 1)
            return ARETURN;
        final char c = desc.charAt(0);
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
