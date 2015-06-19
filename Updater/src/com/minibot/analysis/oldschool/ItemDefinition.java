package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"name", "id","actions","groundActions"})
public class ItemDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheableNode")) && cn.getFieldTypeCount() == 6 &&
                cn.fieldCount("[Ljava/lang/String;") == 2;
    }

    @Override
    public void visit() {
        add("name", cn.getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        visitAll(new Id());
        visitAll(new ActionHooks());
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode rn) {
                    if (rn.opcode() == INVOKESTATIC && rn.desc().startsWith("(Ljava/lang/String;Ljava/lang/String;II")) {
                        NumberNode nn = rn.firstNumber();
                        if (nn != null && nn.number() == 1005) {
                            FieldMemberNode fmn = (FieldMemberNode) rn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                hooks.put("id", new FieldHook("id", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
    private class ActionHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("[Ljava/lang/String;")) {
                        AbstractNode parent = fmn.parent();
                        if (parent != null && parent.opcode() == AASTORE) {
                            NumberNode nn = (NumberNode) parent.layer(ISUB, BIPUSH);
                            if (nn != null && parent.first(INVOKEVIRTUAL) != null) {
                                String name;
                                if (nn.number() == 30) {
                                    name = "groundActions";
                                } else if (nn.number() == 35) {
                                    name = "actions";
                                } else {
                                    return;
                                }
                                if (hooks.containsKey(name))
                                    return;
                                addHook(new FieldHook(name, fmn.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }
}
