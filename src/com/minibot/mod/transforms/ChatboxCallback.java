package com.minibot.mod.transforms;

import com.minibot.client.Callback;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 6/28/2015
 */
public class ChatboxCallback implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.endsWith("V")
                        || !mn.desc.startsWith("(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;"))
                    continue;
                InsnList stack = new InsnList();
                stack.add(new VarInsnNode(ILOAD, 0));
                stack.add(new VarInsnNode(ALOAD, 1));
                stack.add(new VarInsnNode(ALOAD, 2));
                stack.add(new VarInsnNode(ALOAD, 3));
                stack.add(new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                        "messageReceived", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
                mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
            }
        }
    }
}
