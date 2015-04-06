package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.internal.mod.hooks.FieldHook;
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
        add("buckets", cn.getField(null, "[" + desc("Node")), "[" + literalDesc("Node"));
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
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPEQ && jn.first(AALOAD) != null) {
                        FieldMemberNode tail = jn.firstField();
                        String node = desc("Node");
                        if (tail != null && tail.owner().equals(cn.name) && tail.desc().equals(node)) {
                            hooks.put("tail", new FieldHook("tail", tail.fin()));
                            for (FieldNode fn : cn.fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(tail.name())) {
                                        hooks.put("head", new FieldHook("head", fn));
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
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPGE) {
                        FieldMemberNode index = jn.firstField();
                        if (index != null && index.owner().equals(cn.name) && index.desc().equals("I")) {
                            FieldMemberNode size = index.nextField();
                            if (size != null && size.owner().equals(cn.name) && size.desc().equals("I")) {
                                hooks.put("index", new FieldHook("index", index.fin()));
                                hooks.put("size", new FieldHook("size", size.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}

