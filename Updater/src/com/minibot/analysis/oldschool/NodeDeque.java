package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.InvokeHook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@VisitorInfo(hooks = {"tail", "head", "next", "current"})
public class NodeDeque extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fields.size() == 2 && cn.fieldCount(desc("Node")) == 2;
    }

    @Override
    public void visit() {
        visit(new NodeHooks());
	    methods();
    }

    private class NodeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPNE) {
                        FieldMemberNode fmn = jn.firstField();
                        String node = desc("Node");
                        if (fmn != null && fmn.desc().equals(node)) {
                            getHooks().put("tail", new FieldHook("tail", fmn.fin()));
                            for (FieldNode fn : getCn().fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(fmn.name())) {
                                        getHooks().put("head", new FieldHook("head", fn));
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

	private void methods() {
		for (MethodNode mn : getCn().methods) {
			if (mn.desc.equals("()L" + clazz("Node") + ";") && mn.referenced(getUpdater().getArchive().build().get("client"))) {
				int count = 0;
				search: {
					for (AbstractInsnNode ain : mn.instructions.toArray()) {
						if (ain.opcode() == Opcodes.GETFIELD) {
							count++;
						} else if (ain.opcode() == Opcodes.INVOKEVIRTUAL) {
							break search;
						}
					}
					if (count == 4) {
                        getHooks().put("current", new InvokeHook("current", mn));
					} else if (count == 3) {
						getHooks().put("next", new InvokeHook("next", mn));
					}
				}
			}
		}
	}
}