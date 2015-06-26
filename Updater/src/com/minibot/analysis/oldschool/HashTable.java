package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"buckets", "tail", "head", "index", "size"})
public class HashTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        String desc = desc("Node");
        return cn.ownerless() && cn.fieldCount(desc) == 2 && cn.fieldCount("[" + desc) == 1;
    }

    @Override
    public void visit() {
        add("buckets", getCn().getField(null, "[" + desc("Node")), "[" + literalDesc("Node"));
        visit(new NodeHooks());
        visit(new InfoHooks());
    }

    private class NodeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPEQ && jn.first(AALOAD) != null) {
                        FieldMemberNode tail = jn.firstField();
                        String node = desc("Node");
                        if (tail != null && tail.owner().equals(getCn().name) && tail.desc().equals(node)) {
                            getHooks().put("tail", new FieldHook("tail", tail.fin()));
                            for (FieldNode fn : getCn().fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(tail.name())) {
                                        getHooks().put("head", new FieldHook("head", fn));
                                        break;
                                    }
                                }
                            }
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class InfoHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPGE) {
                        FieldMemberNode index = jn.firstField();
                        if (index != null && index.owner().equals(getCn().name) && index.desc().equals("I")) {
                            FieldMemberNode size = index.nextField();
                            if (size != null && size.owner().equals(getCn().name) && size.desc().equals("I")) {
                                getHooks().put("index", new FieldHook("index", index.fin()));
                                getHooks().put("size", new FieldHook("size", size.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}