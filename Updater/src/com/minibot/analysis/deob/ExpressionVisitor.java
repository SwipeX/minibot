package com.minibot.analysis.deob;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;

/**
 * @author Tyler Sedlar
 */
public class ExpressionVisitor extends BlockVisitor {

    private int fixed;

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
            @Override
            public void visitOperation(ArithmeticNode an) {
                if (an.adding() || an.subtracting()) {
                    if (an.firstField() != null) {
                        NumberNode nn = an.firstNumber();
                        if (nn != null) {
                            if (an.adding() && nn.number() < 0) { // # + -5, IADD --> ISUB & abs(LDC)
                                an.insn().setOpcode(an.isInt() ? ISUB : an.isDouble() ? DSUB : an.isLong() ? LSUB : FSUB);
                                nn.setNumber(Math.abs(nn.number()));
                                fixed++;
                            } else if (an.subtracting() && nn.number() < 0) { // # - -3, ISUB --> IADD & abs(LDC)
                                an.insn().setOpcode(an.isInt() ? IADD : an.isDouble() ? DADD : an.isLong() ? LADD : FADD);
                                nn.setNumber(Math.abs(nn.number()));
                                fixed++;
                            }
                        }
                    }
                }
            }
        });
    }
}