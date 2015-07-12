package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.*;

/**
 * @author Tyler Sedlar
 */
@VisitorInfo(hooks = {})
public class Varpbits extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.name.equals("client")) {
            return false;
        }
        if (cn.getFieldTypeCount() == 0) {
            MethodNode clinit = cn.getMethodByName("<clinit>");
            if (clinit == null) {
                return false;
            }
            for (AbstractInsnNode ain : clinit.instructions.toArray()) {
                if (ain instanceof FieldInsnNode) {
                    FieldInsnNode fin = (FieldInsnNode) ain;
                    if (fin.opcode() == PUTSTATIC && fin.desc.equals("[I")) {
                        AbstractInsnNode push = fin.previous().previous();
                        if (push instanceof IntInsnNode && ((IntInsnNode) push).operand == 2000) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {

    }
}