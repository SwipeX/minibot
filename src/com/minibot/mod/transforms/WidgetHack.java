/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.mod.transforms;

import com.minibot.api.method.RuneScape;
import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author Dogerina
 * @since 29-06-2015
 */
public class WidgetHack implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        ClassNode widget = classes.get(ModScript.getClass("Widget"));
        for (ClassNode node : classes.values()) {
            for (MethodNode mn : node.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.endsWith("V")
                        || !mn.desc.startsWith("([L" + widget.name + ";IIIIII"))
                    continue;
                InsnList setStack = new InsnList();
                Label label = new Label();
                LabelNode ln = new LabelNode(label);
                mn.visitLabel(label);
                setStack.add(new InsnNode(ICONST_0));
                setStack.add(new FieldInsnNode(GETSTATIC, RuneScape.class.getName().replace('.', '/'), "WIDGET_RENDERING_ENABLED", "Z"));
                setStack.add(new JumpInsnNode(IFNE, ln));
                setStack.add(new InsnNode(RETURN));
                setStack.add(ln);
                mn.instructions.insert(setStack);
            }
        }
    }
}
