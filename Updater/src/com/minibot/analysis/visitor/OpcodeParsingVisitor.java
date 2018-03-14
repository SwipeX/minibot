package com.minibot.analysis.visitor;

import com.minibot.mod.hooks.FieldHook;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.commons.util.Filter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import java.util.Map;
import java.util.Objects;

public class OpcodeParsingVisitor extends NodeVisitor {

    private static final Filter<AbstractNode> NUMERIC_PREDICATE = an ->
            an.opcode() == SIPUSH || an.opcode() == BIPUSH
                    || an.opcode() == ICONST_1 || an.opcode() == ICONST_2
                    || an.opcode() == ICONST_3 || an.opcode() == ICONST_4
                    || an.opcode() == ICONST_5;
    private static final boolean DEBUG = false;

    private final GraphVisitor parent;
    /* Opcode, Hook name */
    private final Map<Integer, FieldHook> opcodes;

    public OpcodeParsingVisitor(GraphVisitor parent, Map<Integer, FieldHook> opcodes) {
        this.parent = Objects.requireNonNull(parent);
        this.opcodes = Objects.requireNonNull(opcodes);
    }

    @Override
    public boolean validate() {
        for (FieldHook hook : opcodes.values()) {
            if (!parent.getHooks().containsKey(hook.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitJump(JumpNode jn) {
        //search for an if statement comparing a local var to a bipush/sipush
        if (jn.children() == 2 && jn.hasChild(ILOAD) && jn.opcode() != GOTO
                && jn.first(NUMERIC_PREDICATE) != null) {
            int value = jn.firstNumber().number();
            for (Map.Entry<Integer, FieldHook> opcode : opcodes.entrySet()) {
                if (value == opcode.getKey()) {
                    FieldHook fh = opcode.getValue();
                    FieldInsnNode fin = next(jn.insn(), PUTFIELD, fh.getFieldDesc(), parent.getCn().name, 0);
                    if (fin != null) {
                        if (DEBUG) {
                            System.out.println("Opcode: " + value + ", " + fin.owner + "." + fin.name);
                        }
                        fh.setClazz(parent.getCn().name);
                        fh.setField(fin.name);
                        parent.addHook(fh);
                    }
                }
            }
        }
    }

    private static FieldInsnNode next(AbstractInsnNode from, int op, String desc, String owner, int skips) {
        int skipped = 0;
        int maxfollow = 100;
        int follow = 0;
        while ((from = from.next()) != null) {
            if (from.opcode() == op) {
                FieldInsnNode topkek = (FieldInsnNode) from;
                if ((desc == null || topkek.desc.equals(desc)) && (owner == null || owner.equals(topkek.owner))) {
                    if (skipped == skips) {
                        return topkek;
                    }
                    skipped++;
                }
            } else if (from.opcode() == GOTO && follow < maxfollow) {
                from = ((JumpInsnNode) from).label.next();
                follow++;
            }
        }
        return null;
    }
}
