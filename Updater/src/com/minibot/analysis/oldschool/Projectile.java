package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"sequence", "moving", "id", "cycle"})
public class Projectile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderableNode")) && cn.getFieldTypeCount() == 4 &&
                cn.fieldCount("Z") == 1 && cn.fieldCount(desc("AnimationSequence")) == 1;
    }

    @Override
    public void visit() {
        add("moving", getCn().getField(null, "Z"), "Z");
        add("sequence", getCn().getField(null, desc("AnimationSequence")));
        visitAll(new Id());
        visitAll(new Cycle());
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(IMUL, GETFIELD, INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name)) {
                            addHook(new FieldHook("id", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Cycle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == DSTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(I2D, ISUB, IADD, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name)) {
                            addHook(new FieldHook("cycle", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}