package com.minibot.analysis.deob;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class NumericalMutationVisitor extends BlockVisitor {

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
            public void visitOperation(ArithmeticNode an) { // orders by FIELD_MEMBER_NODE NUMBER_NODE ARITHMETIC_NODE
                if (an.children() == 2 && !an.bitwise()) {
                    NumberNode nn = an.firstNumber();
                    if (nn != null) {
                        FieldMemberNode fmn = an.firstField();
                        if (fmn != null) {
                            if (fmn.index() > nn.index()) {
                                AbstractInsnNode operation = an.insn();
                                AbstractInsnNode number = nn.insn();
                                FieldInsnNode field = (FieldInsnNode) fmn.insn();
                                MethodNode mn = an.method();
                                mn.instructions.remove(number);
                                mn.instructions.remove(field);
                                mn.instructions.insertBefore(operation, number);
                                mn.instructions.insertBefore(number, field);
                                fixed++;
                            }
                        }
                    }
                }
            }
        });
    }
}
