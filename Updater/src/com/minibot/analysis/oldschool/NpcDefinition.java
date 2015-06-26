package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.InvokeHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"name", "actions", "id", "transformIds", "transformIndex", "transform"})
public class NpcDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheableNode")) && cn.fieldCount("Z") >= 4 && cn.fieldCount("Z") < 7;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        add("actions", getCn().getField(null, "[Ljava/lang/String;"), "[Ljava/lang/String;");
        visitAll(new Id());
        visitAll(new TransformIds());
        visitAll(new TransformIndex());
        for (MethodNode mn : getCn().methods) {
            if (!Modifier.isStatic(mn.access) && mn.desc.endsWith("L" + getCn().name + ";")) {
                addHook(new InvokeHook("transform", mn));
            }
        }
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    FieldMemberNode cache = mmn.firstField();
                    if (cache != null && cache.desc().equals(desc("Cache"))) {
                        FieldMemberNode id = (FieldMemberNode) mmn.layer(I2L, IMUL, GETFIELD);
                        if (id != null && id.owner().equals(getCn().name)) {
                            addHook(new FieldHook("id", id.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class TransformIds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC && mmn.desc().startsWith("(I")) {
                        FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IALOAD, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name) && fmn.desc().equals("[I")) {
                            if (fmn.first(ALOAD) != null) {
                                getHooks().put("transformIds", new FieldHook("transformIds", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class TransformIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE && vn.var() == 2) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("transformIndex", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}