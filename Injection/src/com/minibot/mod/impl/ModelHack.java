package com.minibot.mod.impl;

import com.minibot.mod.ModScript;
import com.minibot.mod.util.ASMFactory;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.*;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 06:53
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class ModelHack implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        /** Add fields to model supertype **/
        ClassNode rend = classes.get(ModScript.getClass("RenderableNode"));
        rend.fields.add(new FieldNode(ACC_PUBLIC, "drawingDisabled", "Z", null, null));
        rend.methods.add(ASMFactory.createGetter(false, rend.name, "drawingDisabled", "Z", "drawingDisabled"));

        /** Create rendering field setter **/
        MethodNode setter = new MethodNode(ACC_PUBLIC, "setDrawingDisabled", "(Z)V", null, null);
        InsnList stack = new InsnList(); //this.drawingDisabled = var0;
        stack.add(new VarInsnNode(ALOAD, 0));
        stack.add(new VarInsnNode(ILOAD, 1));
        stack.add(new FieldInsnNode(PUTFIELD, rend.name, "drawingDisabled", "Z"));
        stack.add(new InsnNode(RETURN)); //all done
        setter.instructions = stack;
        rend.methods.add(setter);

        ClassNode model = classes.get(ModScript.getClass("Model"));
      /*  List<String> badKeys = new ArrayList<>(); //TODO identify the method in updater module
        for (MethodNode mn : model.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                //onCursorUids[onCursorCount++] = ...;
                if (ain.getOpcode() != IASTORE || !matchPrevs(ain, ILOAD, PUTSTATIC, IADD, ICONST_1, DUP, GETSTATIC, GETSTATIC))
                    continue;
                badKeys.add(mn.name + mn.desc);
            }
        }

        for (MethodNode mn : model.methods) {
            if (badKeys.contains(mn.name + mn.desc) || mn.desc.contains("[B") || Modifier.isStatic(mn.access))
                continue;
            fgt:
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() != IFNONNULL || !matchPrevs(ain, GETFIELD, ALOAD))
                    continue;
                FieldInsnNode field = (FieldInsnNode) ain.getPrevious();
                if (field.desc.equals("[[I")) {
                    VarInsnNode aload = (VarInsnNode) ain.getPrevious().getPrevious();
                    System.out.println(aload.var);
                    InsnList setStack = new InsnList();
                    Label label = new Label();
                    LabelNode ln = new LabelNode(label);
                    mn.visitLabel(label);
                    setStack.add(new InsnNode(ICONST_0));
                    setStack.add(new VarInsnNode(ALOAD, 0));
                    setStack.add(new FieldInsnNode(GETFIELD, rend.name, "drawingDisabled", "Z"));
                    setStack.add(new JumpInsnNode(IFNE, ln));
                    setStack.add(new InsnNode(RETURN));
                    setStack.add(ln); //if (drawingDisabled) return;
                    mn.instructions.insertBefore(aload, setStack);
                    System.out.println("!!!!@" + mn.name + mn.desc);
                    break fgt;
                }
            }
        }*/
    }

    private boolean matchPrevs(AbstractInsnNode ain, int... ops) {
        AbstractInsnNode curr = ain;
        for (int i = 0; i < ops.length && (curr = curr.getPrevious()) != null; i++) {
            if (curr.getOpcode() != ops[i])
                return false;
        }
        return true;
    }
}
