package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.InvokeHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.MemberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

@VisitorInfo(hooks = {"name", "actions", "id", "transformIds", "transformIndex", "transform", "baseColors", "colors",
        "sizeY", "sizeX"})
public class ObjectDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 6 && cn.fieldCount("[S") == 4 && cn.fieldCount("[I") == 4;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        add("actions", getCn().getField(null, "[Ljava/lang/String;"), "[Ljava/lang/String;");
        visit(new Id());
        visit(new TransformIds());
        visit(new TransformIndex());
        visitAll(new Colors());
        visitAll(new SizeHooks());
        getCn().methods.stream().filter(mn -> !Modifier.isStatic(mn.access) &&
                mn.desc.endsWith("L" + getCn().name + ";")).forEach(mn -> addHook(new InvokeHook("transform", mn)));
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETFIELD && fmn.owner().equals(getCn().name) && fmn.desc().equals("I")) {
                        if (fmn.preLayer(IMUL, ISHL, IADD, IADD, I2L) != null) {
                            getHooks().put("id", new FieldHook("id", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class TransformIds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == ARETURN) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(INVOKESTATIC, IALOAD, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name) && fmn.desc().equals("[I")) {
                            getHooks().put("transformIds", new FieldHook("transformIds", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class TransformIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE && vn.var() == 2) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(getCn().name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("transformIndex", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Colors extends BlockVisitor {

        private final Set<Block> blocks = new TreeSet<>((a, b) -> a.index() - b.index());
        private final MemberQuery fieldQuery = new MemberQuery(GETFIELD, getCn().name, "\\[S");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(SALOAD) == 2 && block.count(INVOKEVIRTUAL) == 1 && block.count(fieldQuery) == 2) {
                blocks.add(block);
            }
        }

        @Override
        public void visitEnd() {
            if (blocks.isEmpty()) {
                return;
            }
            Block valid = blocks.toArray(new Block[blocks.size()])[0];
            FieldInsnNode baseColors = (FieldInsnNode) valid.get(fieldQuery, 0);
            FieldInsnNode colors = (FieldInsnNode) valid.get(fieldQuery, 1);
            if (baseColors != null && colors != null) {
                addHook(new FieldHook("baseColors", baseColors));
                addHook(new FieldHook("colors", colors));
                lock.set(true);
            }
        }
    }

    private class SizeHooks extends BlockVisitor {

        private final MemberQuery fieldQuery = new MemberQuery(GETFIELD, getCn().name, "I");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(IADD) == 1 && block.count(IAND) == 2 && block.count(fieldQuery) == 2) {
                FieldInsnNode sizeY = (FieldInsnNode) block.get(fieldQuery, 0);
                FieldInsnNode sizeX = (FieldInsnNode) block.get(fieldQuery, 1);
                if (sizeY != null && sizeX != null) {
                    addHook(new FieldHook("sizeY", sizeY));
                    addHook(new FieldHook("sizeX", sizeX));
                }
            }
        }
    }
}