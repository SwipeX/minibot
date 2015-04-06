package com.minibot.analysis.deob;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class ComparisonVisitor extends BlockVisitor {

    private int fixed = 0;

    @Override
    public String toString() {
        return Integer.toString(fixed);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void visit(Block block) {
        block.tree().accept(new NodeVisitor() {
            public void visitJump(JumpNode jn) {
                if (jn.children() == 2) {
                    if (jn.opcode() == IF_ACMPEQ || jn.opcode() == IF_ACMPNE) {
                        AbstractNode n = jn.first(ACONST_NULL);
                        if (n == null) return;
                        AbstractNode swap = jn.firstField();
                        if (swap != null && !((FieldMemberNode) swap).getting()) {
                            return;
                        } else if (swap == null) {
                            swap = jn.first(ALOAD);
                            if (swap == null) return;
                        }
                        if (n.index() < swap.index()) {
                            jn.insn().setOpcode(jn.opcode() == IF_ACMPEQ ? IFNULL : IFNONNULL);
                            jn.method().instructions.remove(n.insn());
                            fixed++;
                        }
                    } else {
                        AbstractNode n = jn.first();
                        if (n != null && n instanceof NumberNode) {
                            AbstractNode swap = jn.firstField();
                            AbstractNode other = null;
                            if (swap != null && !((FieldMemberNode) swap).getting()) {
                                return;
                            } else if (swap == null) {
                                other = jn.firstOperation();
                                if (other == null) return;
                                swap = other.firstField();
                                if (swap == null) return;
                            }
                            if (n.index() < swap.index()) {
                                MethodNode mn = jn.method();
                                AbstractInsnNode ain = n.insn();
                                mn.instructions.remove(ain);
                                AbstractNode farthest = swap;
                                if (other != null) {
                                    if (other.index() > swap.index()) farthest = other;
                                }
                                mn.instructions.insert(farthest.insn(), ain);
                                fixed++;
                            }
                        }
                    }
                }
            }
        });
    }
}
