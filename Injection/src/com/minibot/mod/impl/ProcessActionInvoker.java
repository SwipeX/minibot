package com.minibot.mod.impl;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 06:13
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class ProcessActionInvoker implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        InvokeHook meth = ModScript.getInvokeHook("Client#processAction");
        if (meth == null)
            throw new RuntimeException("#processAction hook broke?");
        ClassNode client = classes.get("client");
        MethodNode invoker = new MethodNode(ACC_PUBLIC, "processAction", "(IIIILjava/lang/String;Ljava/lang/String;II)V", null, null);
        InsnList stack = new InsnList();
        stack.add(new VarInsnNode(Opcodes.ILOAD, 0));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 1));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 2));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 3));
        stack.add(new VarInsnNode(Opcodes.ALOAD, 4));
        stack.add(new VarInsnNode(Opcodes.ALOAD, 5));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 6));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 7));
        if (meth.predicate != Integer.MAX_VALUE)
            stack.add(new LdcInsnNode(meth.predicate));
        stack.add(new MethodInsnNode(INVOKESTATIC, meth.clazz, meth.method, meth.desc, false));
        System.out.println("...Injected processAction invoker!");
    }
}
