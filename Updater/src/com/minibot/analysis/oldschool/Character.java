package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.internal.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.NumberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"x", "y", "health", "maxHealth", "interactingIndex", "animation"})
public class Character extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderableNode")) && cn.fieldCount("[I") == 5 && cn.fieldCount("Z") >= 1 &&
                cn.fieldCount("Ljava/lang/String;") == 1;
    }

    @Override
    public void visit() {
        visitAll(new PositionHooks());
        visitAll(new HealthHooks());
        visitAll(new InteractingIndex());
        visitAll(new Animation());
    }

    private class PositionHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 13184)) > 0) {
                block.tree().accept(new NodeVisitor() {
                    public void visitJump(JumpNode jn) {
                        String name = null;
                        if (jn.opcode() == IF_ICMPGE) {
                            name = "x";
                        } else if (jn.opcode() == IF_ICMPLT) {
                            name = "y";
                        }
                        if (name == null || hooks.containsKey(name))
                            return;
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                        if (fmn == null)
                            return;
                        addHook(new FieldHook(name, fmn.fin()));
                        added++;
                    }
                });
            }
        }
    }

    private class HealthHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IDIV) {
                        an = an.firstOperation();
                        if (an != null && an.opcode() == IMUL) {
                            FieldMemberNode health = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                            if (health != null && health.opcode() == GETFIELD && health.owner().equals(cn.name) &&
                                    health.desc().equals("I")) {
                                an = an.nextOperation();
                                if (an != null && an.opcode() == IMUL) {
                                    FieldMemberNode max = an.firstField();
                                    if (max != null && max.opcode() == GETFIELD && max.owner().equals(cn.name) &&
                                            max.desc().equals("I")) {
                                        hooks.put("health", new FieldHook("health", health.fin()));
                                        hooks.put("maxHealth", new FieldHook("maxHealth", max.fin()));
                                        lock.set(true);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class InteractingIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == AALOAD) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) n.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                                hooks.put("interactingIndex", new FieldHook("interactingIndex", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Animation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("AnimationSequence")) && fmn.desc().equals("I")) {
                        NumberNode nn = (NumberNode) fmn.layer(INVOKESTATIC, IMUL, LDC);
                        fmn = (FieldMemberNode) fmn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && nn != null) {
                            FieldHook fh = new FieldHook("animation", fmn.fin());
                            fh.multiplier = nn.number();
                            addHook(fh);
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}