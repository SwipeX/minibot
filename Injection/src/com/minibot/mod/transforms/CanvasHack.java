package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class CanvasHack implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        ClassNode cn = classes.get(ModScript.getClass("Canvas"));
        cn.superName = "com/minibot/client/GameCanvas";
        nig:
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("<init>"))
                continue;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain instanceof MethodInsnNode) {
                    MethodInsnNode meth = (MethodInsnNode) ain;
                    if (!meth.owner.contains("Canvas"))
                        continue;
                    meth.owner = "com/minibot/client/GameCanvas";
                    break nig;
                }
            }
        }
    }
}
