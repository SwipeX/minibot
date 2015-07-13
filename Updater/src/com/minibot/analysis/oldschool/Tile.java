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

@VisitorInfo(hooks = {"objects", "x", "y", "plane", "wallDecoration", "floorDecoration", "boundary"})
public class Tile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("Z") == 3;
    }

    @Override
    public void visit() {
        add("objects", getCn().getField(null, "[" + desc("InteractableObject")), "[" + literalDesc("InteractableObject"));
        add("wallDecoration", getCn().getField(null, desc("WallDecoration")), literalDesc("WallDecoration"));
        add("floorDecoration", getCn().getField(null, desc("FloorDecoration")), literalDesc("FloorDecoration"));
        add("boundary", getCn().getField(null, desc("Boundary")), literalDesc("Boundary"));
        visit(new TileHooks());
    }

    private class TileHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(getCn().name) && fmn.desc().equals("I")) {
                        VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                        if (vn == null) {
                            vn = (VariableNode) fmn.layer(DUP_X1, IMUL, ILOAD);
                        }
                        if (vn != null) {
                            String name = null;
                            if (vn.var() == 1) {
                                name = "plane";
                            } else if (vn.var() == 2) {
                                name = "x";
                            } else if (vn.var() == 3) {
                                name = "y";
                            }
                            if (name == null) {
                                return;
                            }
                            getHooks().put(name, new FieldHook(name, fmn.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }
}