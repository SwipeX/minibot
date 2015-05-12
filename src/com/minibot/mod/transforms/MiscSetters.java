package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.FieldHook;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

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
        setter.instructions.add(new FieldInsnNode(PUTSTATIC, hook.clazz, hook.field, hook.fieldDesc));
        setter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(setter);
    }
}
