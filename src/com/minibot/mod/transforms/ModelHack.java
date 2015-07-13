package com.minibot.mod.transforms;

import com.minibot.api.method.RuneScape;
import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelHack implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        //landscape
        ClassNode landscape = classes.get(ModScript.getClass("Region"));
        if (landscape == null) {
            throw new RuntimeException("wat");
        }
        for (MethodNode methodNode : landscape.methods) {
            if (methodNode.desc.startsWith("(IIIIII")
                    && methodNode.desc.endsWith("V")
                    && Modifier.isPublic(methodNode.access)) {
                InsnList setStack = new InsnList();
                Label label = new Label();
                LabelNode ln = new LabelNode(label);
                methodNode.visitLabel(label);
                setStack.add(new InsnNode(ICONST_0));
                setStack.add(new FieldInsnNode(GETSTATIC, RuneScape.class.getName().replace('.', '/'), "LANDSCAPE_RENDERING_ENABLED", "Z"));
                setStack.add(new JumpInsnNode(IFNE, ln));
                setStack.add(new InsnNode(RETURN));
                setStack.add(ln);
                methodNode.instructions.insert(setStack);
                //  System.out.println("Injected conditional disable landscape rendering @" + methodNode.name + methodNode.desc);
            }
            //Model
            ClassNode model = classes.get(ModScript.getClass("Model"));
            List<String> badKeys = new ArrayList<>(); //TODO identify the method in updater module
            for (MethodNode mn : model.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    //onCursorUids[onCursorCount++] = ...;
                    if (ain.getOpcode() != IASTORE || !matchPrevs(ain, ILOAD, PUTSTATIC, IADD, ICONST_1, DUP, GETSTATIC, GETSTATIC)) {
                        continue;
                    }
                    badKeys.add(mn.name + mn.desc);
                }
            }
            for (MethodNode mn : model.methods) {
                if (badKeys.contains(mn.name + mn.desc) || mn.desc.contains("[B") || Modifier.isStatic(mn.access)) {
                    continue;
                }
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != IFNONNULL || !matchPrevs(ain, GETFIELD, ALOAD)) {
                        continue;
                    }
                    FieldInsnNode field = (FieldInsnNode) ain.getPrevious();
                    if (field.desc.equals("[B")) {
                        VarInsnNode aload = (VarInsnNode) ain.getPrevious().getPrevious();
                        InsnList setStack = new InsnList();
                        Label label = new Label();
                        LabelNode ln = new LabelNode(label);
                        mn.visitLabel(label);
                        setStack.add(new InsnNode(ICONST_0));
                        setStack.add(new FieldInsnNode(GETSTATIC, RuneScape.class.getName().replace('.', '/'), "MODEL_RENDERING_ENABLED", "Z"));
                        setStack.add(new JumpInsnNode(IFNE, ln));
                        setStack.add(new InsnNode(RETURN));
                        setStack.add(ln);
                        mn.instructions.insertBefore(aload, setStack);
                        //  System.out.println("Injected conditional disable model rendering @" + mn.name + mn.desc);
                        //break fgt;
                    }
                }
            }
        }
    }


    private boolean matchPrevs(AbstractInsnNode ain, int... ops) {
        AbstractInsnNode curr = ain;
        for (int i = 0; i < ops.length && (curr = curr.getPrevious()) != null; i++) {
            if (curr.getOpcode() != ops[i]) {
                return false;
            }
        }
        return true;
    }
}