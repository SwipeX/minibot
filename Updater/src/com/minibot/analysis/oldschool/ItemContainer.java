package com.minibot.analysis.oldschool;


import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.ArrayList;
import java.util.List;

@VisitorInfo(hooks = {"ids", "stackSizes"})
public class ItemContainer extends GraphVisitor {
 
    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 1 && cn.fieldCount("[I") == 2 && cn.superName.equals(clazz("Node"));
    }
 
    @Override
    public void visit() {
        visitAll(new Hooks());
    }
 
    private class Hooks extends BlockVisitor {
 
        private final List<FieldMemberNode> vars = new ArrayList<>();
 
        @Override
        public boolean validate() {
            return vars.size() < 2 || !lock.get();
        }
 
        @Override
        public void visit(Block block) {
            if ((block.owner.access & ACC_STATIC) == 0) {
                return;
            }
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.owner().equals(clazz("HashTable")) && mmn.desc().startsWith("(J")
                            && mmn.hasChild(I2L) && mmn.hasChild(GETSTATIC) && mmn.nextType() != null
                            && mmn.nextType().type().equals(getCn().name)) {
                        getUpdater().visitor("Client").addHook(new FieldHook("itemContainers", mmn.firstField().fin()));
                        lock.set(true);
                    }
                }

                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (vars.size() < 2 && fmn.owner().equals(getCn().name) && fmn.desc().equals("[I") && fmn.putting()) {
                        VariableNode value = (VariableNode) fmn.last(ALOAD);
                        if (value == null) {
                            return;
                        }
                        vars.add(fmn);
                    }
                }
            });
        }
 
        @Override
        public void visitEnd() {
            if (vars.size() < 2) {
                return;
            }
            vars.sort((a, b) -> {
                VariableNode vA = (VariableNode) a.last(ALOAD);
                VariableNode vB = (VariableNode) b.last(ALOAD);
                return vA.var() - vB.var();
            });
            addHook(new FieldHook("ids", vars.get(0).fin()));
            addHook(new FieldHook("stackSizes", vars.get(1).fin()));
        }
    }
}