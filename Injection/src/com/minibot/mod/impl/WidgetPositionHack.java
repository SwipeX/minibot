package com.minibot.mod.impl;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.util.ASMFactory;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 21:33
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class WidgetPositionHack implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        ClassNode widget = classes.get(ModScript.getClass("Widget"));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerX", "I", null, null));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerY", "I", null, null));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerX", "I", "containerX"));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerY", "I", "containerY"));
        FieldHook x = ModScript.getFieldHook("Widget#x");
        FieldHook y = ModScript.getFieldHook("Widget#y");
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.startsWith("([L" + widget.name + ";IIIIII"))
                    continue;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != GETFIELD)
                        continue;
                    FieldInsnNode fin = (FieldInsnNode) ain;
                    if ((fin.name.equals(x.field) && fin.owner.equals(x.clazz)) && (fin.name.equals(y.field)) && fin.owner.equals(y.clazz)) {
                        VarInsnNode widgetVar = (VarInsnNode) prev(ain, ALOAD);
                        VarInsnNode store = (VarInsnNode) next(ain, ISTORE);
                        if (widgetVar == null || store == null)
                            break;
                        InsnList stack = new InsnList();
                        boolean _x = fin.name.equals(x.field);
                        stack.add(new VarInsnNode(ALOAD, widgetVar.var));
                        stack.add(new VarInsnNode(ILOAD, _x ? store.var - 1 : store.var));
                        stack.add(new FieldInsnNode(PUTFIELD, widget.name, _x ? "containerX" : "containerY", "I"));
                        mn.instructions.insertBefore(store, stack);
                    }
                }
            }
        }
    }

    private AbstractInsnNode next(AbstractInsnNode curr, int op) {
        AbstractInsnNode ok = curr;
        while ((ok = ok.getNext()) != null) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }

    private AbstractInsnNode prev(AbstractInsnNode curr, int op) {
        AbstractInsnNode ok = curr;
        while ((ok = ok.getPrevious()) != null) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }
}
