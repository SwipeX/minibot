package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.InsnQuery;
import org.objectweb.asm.commons.cfg.query.MemberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@VisitorInfo(hooks = {"owner", "children", "x", "y", "width", "height", "itemId", "itemAmount",
        "id", "type", "itemIds", "stackSizes", "scrollX", "scrollY", "textureId", "index",
        "text", "ownerId", "hidden", "boundsIndex"})
public class Widget extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("[Ljava/lang/Object;") > 10;
    }

    @Override
    public void visit() {
        add("owner", cn.getField(null, "L" + cn.name + ";"), literalDesc("Widget"));
        add("children", cn.getField(null, "[L" + cn.name + ";"), "[" + literalDesc("Widget"));
        visitAll(new PositionHooks());
        visitAll(new SizeHooks());
        visitAll(new TradeHooks());
        visitAll(new IdHooks());
        visitAll(new Type());
        visitAll(new ItemIds());
        visitAll(new StackSizes());
        visitAll(new ScrollHooks());
        visitAll(new TextureId());
        visitAll(new Index());
        visitAll(new Text());
        visitAll(new Hidden());
        visitAll(new BoundsIndex());
    }

    private class PositionHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (!fmn.owner().equals(clazz("Widget"))) return;
                    AbstractNode n = fmn.parent();
                    if (n != null) n = n.parent();
                    if (n == null) return;
                    if (n.opcode() == IADD && n.hasParent() && n.parent().opcode() == IASTORE) {
                        VariableNode vn = n.firstVariable();
                        if (vn != null && vn.opcode() == ILOAD) {
                            String name = null;
                            if (vn.var() == 6) {
                                name = "x";
                            } else if (vn.var() == 7) {
                                name = "y";
                            }
                            if (name == null) return;
                            if (!hooks.containsKey(name)) {
                                hooks.put(name, new FieldHook(name, fmn.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }

    private class SizeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().matches("\\(IIII(I|B|S)\\)V")) {
                        ArithmeticNode an = (ArithmeticNode) mmn.first(IMUL);
                        if (an != null) {
                            FieldMemberNode width = an.firstField();
                            if (width != null && width.opcode() == GETFIELD && width.owner().equals(cn.name)) {
                                an = an.nextOperation();
                                if (an != null && an.opcode() == IMUL) {
                                    FieldMemberNode height = an.firstField();
                                    if (height != null && height.opcode() == GETFIELD && height.owner().equals(cn.name)) {
                                        hooks.put("width", new FieldHook("width", width.fin()));
                                        hooks.put("height", new FieldHook("height", height.fin()));
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

    private class TradeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(IIIIIZ")) {
                        for (int i = 0; i < 3; i++) {
                            if (mmn.child(i) == null || mmn.child(i).opcode() != IMUL) return;
                        }
                        FieldMemberNode id = mmn.child(0).firstField();
                        FieldMemberNode amount = mmn.child(1).firstField();
                        FieldMemberNode thickness = mmn.child(2).firstField();
                        if (id == null || amount == null || thickness == null)
                            return;
                        hooks.put("itemId", new FieldHook("itemId", id.fin()));
                        hooks.put("itemAmount", new FieldHook("itemAmount", amount.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class IdHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        AbstractNode n = fmn.layer(IMUL, PUTFIELD, DUP_X1, IMUL, GETFIELD, ALOAD);
                        if (n != null) {
                            FieldMemberNode id = (FieldMemberNode) n.parent();
                            hooks.put("ownerId", new FieldHook("ownerId", fmn.fin()));
                            hooks.put("id", new FieldHook("id", id.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Type extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            if (block.count(AASTORE) == 1 && block.count(DUP_X1) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                            VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                            if (vn != null && vn.var() == 17) {
                                addHook(new FieldHook("type", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }

    private class ItemIds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().endsWith(desc("ItemDefinition"))) {
                        FieldMemberNode fmn = (FieldMemberNode) mmn.layer(ISUB, IALOAD, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            hooks.put("itemIds", new FieldHook("itemIds", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class StackSizes extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.isStatic() && mmn.desc().startsWith("(IIIIIZ")) {
                        FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IALOAD, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.desc().equals("[I")) {
                            hooks.put("stackSizes", new FieldHook("stackSizes", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class ScrollHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISUB) {
                        VariableNode vn = an.firstVariable();
                        if (vn != null) {
                            String name = null;
                            if (vn.var() == 13) {
                                name = "scrollX";
                            } else if (vn.var() == 14) {
                                name = "scrollY";
                            }
                            if (name == null) return;
                            FieldMemberNode fmn = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                if (!hooks.containsKey(name)) {
                                    hooks.put(name, new FieldHook(name, fmn.fin()));
                                    added++;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class TextureId extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        private boolean collected = false;
        private final Set<String> possible = new HashSet<>();

        @Override
        public void visit(Block block) {
            final Map<String, Integer> counts = new HashMap<>();
            if (!collected) {
                visitAll(new BlockVisitor() {
                    public boolean validate() {
                        return true;
                    }

                    public void visit(Block block) {
                        if (block.count(new MemberQuery(PUTFIELD, cn.name, "I")) == 1 &&
                                block.count(new InsnQuery(ALOAD)) == 1) {
                            NodeTree root = block.tree();
                            FieldMemberNode fmn = root.firstField();
                            if (fmn != null && fmn.opcode() == PUTFIELD && fmn.layer(IMUL, GETSTATIC) != null) {
                                int count = 1;
                                if (counts.containsKey(fmn.key()))
                                    count += counts.get(fmn.key());
                                counts.put(fmn.key(), count);
                                possible.add(fmn.key());
                            }
                        }
                    }
                });
                collected = true;
                counts.entrySet().stream().filter(entry -> entry.getValue() < 3).forEach(entry ->
                        possible.remove(entry.getKey())
                );
            }
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                        fmn = (FieldMemberNode) fmn.layer(IMUL, GETFIELD);
                        if (fmn != null && possible.contains(fmn.key())) {
                            addHook(new FieldHook("textureId", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class Index extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPEQ) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(AALOAD, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            hooks.put("index", new FieldHook("index", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Text extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.name().equals("equals") && mmn.first(ALOAD) != null) {
                        FieldMemberNode fmn = mmn.firstField();
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("text", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Hidden extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == IRETURN) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.desc().equals("Z")) {
                            addHook(new FieldHook("hidden", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class BoundsIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visit(AbstractNode n) {
                    if (n.opcode() == BASTORE) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.desc().equals("[Z")) {
                            FieldMemberNode index = (FieldMemberNode) n.layer(IMUL, GETFIELD);
                            if (index != null && index.owner().equals(cn.name)) {
                                addHook(new FieldHook("boundsIndex", index.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}

