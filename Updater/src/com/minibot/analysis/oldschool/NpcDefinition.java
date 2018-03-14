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
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"name", "actions", "id", "transformIds", "transformIndex", "level", "transform"})
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
        visitAll(new Level());
        getCn().methods.stream().filter(
                mn -> !Modifier.isStatic(mn.access) && mn.desc.endsWith("L" + getCn().name + ";")
        ).forEach(mn -> addHook(new InvokeHook("transform", mn)));
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
                public void visit(AbstractNode n) {
                    if (n.opcode() == IALOAD) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(ISUB, ARRAYLENGTH, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name)) {
                            addHook(new FieldHook("transformIds", fmn.fin()));
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

    private class Level extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitAny(AbstractNode an) {
                    FieldMemberNode fmn = (FieldMemberNode) an.layer(IMUL, GETFIELD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals(desc("Player"))) {
                        an = an.layer(IMUL, GETFIELD, ALOAD);
                        if (an != null) {
                            fmn = (FieldMemberNode) an.parent();
                            if (fmn != null && fmn.owner().equals(clazz("NpcDefinition"))) {
                                addHook(new FieldHook("level", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}