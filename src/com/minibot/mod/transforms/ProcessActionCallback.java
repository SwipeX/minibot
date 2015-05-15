package com.minibot.mod.transforms;

import com.minibot.client.Callback;
import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class ProcessActionCallback implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        InvokeHook meth = ModScript.getInvokeHook("Client#processAction");
        if (meth == null)
            throw new RuntimeException("#processAction hook broke?");
        for (ClassNode cn : classes.values()) {
            if (!cn.name.equals(meth.clazz))
                continue;
            for (MethodNode mn : cn.methods) {
                if (!mn.name.equals(meth.method) || !mn.desc.equals(meth.desc))
                    continue;
                InsnList stack = new InsnList();
                stack.add(new VarInsnNode(Opcodes.ILOAD, 0));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 1));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 2));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 3));
                stack.add(new VarInsnNode(Opcodes.ALOAD, 4));
                stack.add(new VarInsnNode(Opcodes.ALOAD, 5));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 6));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 7));
                stack.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Callback.class.getName().replace('.', '/'), "processAction",
                        "(IIIILjava/lang/String;Ljava/lang/String;II)V"));
                mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
                System.out.println("...Injected processAction callback!");
            }
        }
    }
}
