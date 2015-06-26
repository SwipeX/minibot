package com.minibot.mod.transforms;

import com.minibot.client.Callback;
import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.FieldHook;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * @author Tim Dekker
 * @since 5/12/15
 */
public class MiscSetters implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        //setter -> resetMouseIdleTime
        FieldHook hook = ModScript.getFieldHook("Client#mouseIdleTime");
        if (hook == null)
            throw new RuntimeException("hook broke?");
        MethodNode setter = new MethodNode(ACC_PUBLIC, "resetMouseIdleTime", "()V", null, null);
        setter.instructions.add(new InsnNode(ICONST_0));
        setter.instructions.add(new FieldInsnNode(PUTSTATIC, hook.getClazz(), hook.getField(), hook.getFieldDesc()));
        setter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(setter);
        //setter -> login
        ClassNode client = classes.get("client");
        client.methods.add(mkStringSetter("setUsername", ModScript.getFieldHook("Client#username")));
        client.methods.add(mkStringSetter("setPassword", ModScript.getFieldHook("Client#password")));
        //onEngineTick
        ClassNode engine = classes.get(classes.get("client").superName);
        for (MethodNode run : engine.methods) {
            if (!run.name.equals("run") || !run.desc.equals("()V"))
                continue;
            for (AbstractInsnNode ain : run.instructions.toArray()) {
                if (ain.getOpcode() == PUTSTATIC && backtrack(ain, INVOKEVIRTUAL)) {
                    run.instructions.insert(ain, new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                            "onEngineTick", "()V", false));
                }
            }
        }
    }


    private MethodNode mkStringSetter(String name, FieldHook hook) {
        MethodNode meth = new MethodNode(ACC_PUBLIC, name, "(Ljava/lang/String;)V", null, null);
        meth.instructions.add(new VarInsnNode(ALOAD, 1));
        meth.instructions.add(new FieldInsnNode(PUTSTATIC, hook.getClazz(), hook.getField(), "Ljava/lang/String;"));
        meth.instructions.add(new InsnNode(RETURN));
        return meth;
    }

    private boolean backtrack(AbstractInsnNode ain, int insn) {
        for (int i = 0; i < 5 && (ain = ain.getPrevious()) != null; i++) {
            if (ain.getOpcode() == insn)
                return true;
        }
        return false;
    }
}