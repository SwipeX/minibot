package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"sentinel", "tail"})
public class LinkedList extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount() == 2 && cn.fieldCount(desc("Node")) == 2 && cn.interfaces.size() == 1;
    }

    @Override
    public void visit() {
        visit(new Sentinel());
        for (FieldNode fn : getCn().fields) {
            if ((fn.access & ACC_STATIC) == 0 && !fn.name.equals(((FieldHook) getHooks().get("sentinel")).getField())) {
                add("tail", fn);
                break;
            }
        }
    }

    private class Sentinel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.owner.name.equals("<init>")) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTFIELD) {
                            AbstractNode neww = fmn.layer(INVOKESPECIAL, DUP, NEW);
                            if (neww != null) {
                                addHook(new FieldHook("sentinel", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }
}
