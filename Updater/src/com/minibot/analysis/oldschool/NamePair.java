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

@VisitorInfo(hooks = {"raw", "formatted"})
public class NamePair extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces != null && cn.interfaces.contains("java/lang/Comparable")
                && cn.fieldCount("Ljava/lang/String;") == 2 && cn.fieldCount() == 2;
    }

    @Override
    public void visit() {
        visit(new Hooks());
    }

    private class Hooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.owner.name.equals("<init>") && block.owner.desc.startsWith("(Ljava/lang/String;")) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTFIELD) {
                            VariableNode vn = (VariableNode) fmn.last(ALOAD);
                            if (vn != null) {
                                if (vn.var() == 1) {
                                    addHook(new FieldHook("raw", fmn.fin()));
                                } else if (vn.var() > 1) {
                                    addHook(new FieldHook("formatted", fmn.fin()));
                                }
                            }

                            if (fmn.hasChild(INVOKESTATIC)) { //sometimes it gets inlined, so the above var check is needed
                                addHook(new FieldHook("formatted", fmn.fin()));
                            }
                        }
                    }
                });
            }
        }
    }
}
