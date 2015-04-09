package com.minibot.mod.impl;

import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 21:26
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
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
