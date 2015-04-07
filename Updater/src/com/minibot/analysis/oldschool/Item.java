package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"id", "stackSize"})
public class Item extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderableNode")) && cn.getFieldTypeCount() == 1 && cn.fieldCount("I") == 2;
    }

    @Override
    public void visit() {
        visit(new InfoHooks());
    }

    private class InfoHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().startsWith("(I")) {
                        FieldMemberNode id = (FieldMemberNode) mmn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (id != null && id.owner().equals(cn.name) && id.desc().equals("I")) {
                            FieldMemberNode stack = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                            if (stack != null && stack.desc().equals("I")) {
                                hooks.put("id", new FieldHook("id", id.fin()));
                                hooks.put("stackSize", new FieldHook("stackSize", stack.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}
