package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"hitsplats"})
public class HealthBar extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("LinkedList")) == 1
                && cn.superName.equals(clazz("Node"))
                && cn.interfaces.size() == 0;
    }

    @Override
    public void visit() {
        add("hitsplats", getCn().getField(null, desc("LinkedList")));
    }
}
