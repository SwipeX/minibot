package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@VisitorInfo(hooks = {"x", "y", "healthBars", "interactingIndex", "animation", "orientation"})
public class Character extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderableNode"))
                && cn.fieldCount("[B") == 1
                && cn.fieldCount("Z") > 1;
    }

    @Override
    public void visit() {
        add("healthBars", getCn().getField(null, desc("LinkedList")));
        visitIfM(new PositionHooks(), m -> m.desc.startsWith("(L" + getCn().name + ";I") && (m.access & ACC_STATIC) != 0);
        //visitAll(new HealthHooks());
        visitAll(new InteractingIndex());
        visitAll(new Animation());
        visitAll(new Orientation());
    }

    private class PositionHooks extends BlockVisitor {

        private int added;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.children() >= 3) {
                        List<AbstractNode> delegates = mmn.layerAll(IMUL, GETFIELD);
                        if (delegates == null || delegates.size() != 2) {
                            return;
                        }
                        for (AbstractNode delegate : delegates) {
                            FieldMemberNode fmn = (FieldMemberNode) delegate;
                            if (!fmn.owner().equals(getCn().name)) {
                                continue;
                            }
                            addHook(new FieldHook(getHooks().containsKey("x") ? "y" : "x", fmn.fin()));
                            added++;
                        }
                    }
                }
            });
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
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IDIV) {
                        an = an.firstOperation();
                        if (an != null && an.opcode() == IMUL) {
                            FieldMemberNode health = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                            if (health != null && health.opcode() == GETFIELD && health.owner().equals(getCn().name) &&
                                    health.desc().equals("I")) {
                                an = an.nextOperation();
                                if (an != null && an.opcode() == IMUL) {
                                    FieldMemberNode max = an.firstField();
                                    if (max != null && max.opcode() == GETFIELD && max.owner().equals(getCn().name) &&
                                            max.desc().equals("I")) {
                                        getHooks().put("health", new FieldHook("health", health.fin()));
                                        getHooks().put("maxHealth", new FieldHook("maxHealth", max.fin()));
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
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == AALOAD) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) n.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(getCn().name) && fmn.desc().equals("I")) {
                                getHooks().put("interactingIndex", new FieldHook("interactingIndex", fmn.fin()));
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
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("AnimationSequence")) && fmn.desc().equals("I")) {
                        NumberNode nn = (NumberNode) fmn.layer(INVOKESTATIC, IMUL, LDC);
                        fmn = (FieldMemberNode) fmn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && nn != null) {
                            FieldHook fh = new FieldHook("animation", fmn.fin());
                            fh.setMultiplier(nn.number());
                            addHook(fh);
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Orientation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(getCn().name)) {
                        if (fmn.layer(IMUL, IAND, D2I, DMUL, INVOKESTATIC, I2D) != null) {
                            addHook(new FieldHook("orientation", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}