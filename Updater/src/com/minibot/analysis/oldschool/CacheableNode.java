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

@VisitorInfo(hooks = {"previous", "next"})
public class CacheableNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("L" + cn.name + ";") == 2;
    }

    @Override
    public void visit() {
        visit(new NodeHooks());
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
                    if (jn.opcode() == IFNONNULL) {
                        FieldMemberNode fmn = jn.firstField();
                        if (fmn != null && fmn.desc().equals("L" + cn.name + ";")) {
                            hooks.put("previous", new FieldHook("previous", fmn.fin()));
                            for (FieldNode fn : cn.fields) {
                                if (fn.desc.equals("L" + cn.name + ";")) {
                                    if (!fn.name.equals(fmn.name())) {
                                        hooks.put("next", new FieldHook("next", fn));
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
}
