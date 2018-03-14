package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"namePair"})
public class Player extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Character"))
                && cn.fieldCount("Z") >= 1;
    }

    @Override
    public void visit() {
        add("namePair", getCn().getField(null, desc("NamePair")));
    }
}