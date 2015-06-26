package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"name"})
public class Player extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Character")) && cn.fieldCount("Ljava/lang/String;") >= 1 &&
                cn.fieldCount("Z") >= 1 && cn.getAbnormalFieldCount() == 2;
    }

    @Override
    public void visit() {
        add("name", getCn().getField(null, "Ljava/lang/String;"));
    }
}