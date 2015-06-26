package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.InvokeHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"name", "actions", "id", "transformIds", "transformIndex", "transform"})
public class ObjectDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 6 && cn.fieldCount("[S") == 4 && cn.fieldCount("[I") == 4;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        add("actions", getCn().getField(null, "[Ljava/lang/String;"), "[Ljava/lang/String;");
        visit(new Id());
        visit(new TransformIds());
        visit(new TransformIndex());
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
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETFIELD && fmn.owner().equals(getCn().name) && fmn.desc().equals("I")) {
                        if (fmn.preLayer(IMUL, ISHL, IADD, IADD, I2L) != null) {
                            getHooks().put("id", new FieldHook("id", fmn.fin()));
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
                public void visit(AbstractNode n) {
                    if (n.opcode() == ARETURN) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(INVOKESTATIC, IALOAD, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name) && fmn.desc().equals("[I")) {
                            getHooks().put("transformIds", new FieldHook("transformIds", fmn.fin()));
                            lock.set(true);
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