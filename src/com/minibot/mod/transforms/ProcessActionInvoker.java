package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class ProcessActionInvoker implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        InvokeHook meth = ModScript.getInvokeHook("Client#processAction");
        if (meth == null)
            throw new RuntimeException("#processAction hook broke?");
        ClassNode client = classes.get("client");
        MethodNode invoker = new MethodNode(ACC_PUBLIC, "processAction", "(IIIILjava/lang/String;Ljava/lang/String;II)V", null, null);
        InsnList stack = new InsnList();
        stack.add(new VarInsnNode(Opcodes.ILOAD, 1));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 2));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 3));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 4));
        stack.add(new VarInsnNode(Opcodes.ALOAD, 5));
        stack.add(new VarInsnNode(Opcodes.ALOAD, 6));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 7));
        stack.add(new VarInsnNode(Opcodes.ILOAD, 8));
        if (meth.predicate != Integer.MAX_VALUE)
            stack.add(new LdcInsnNode(meth.predicate));
        stack.add(new MethodInsnNode(INVOKESTATIC, meth.clazz, meth.method, meth.desc, false));
        stack.add(new InsnNode(RETURN));
        invoker.instructions = stack;
        client.methods.add(invoker);
        System.out.println("...Injected processAction invoker!");
    }
}
