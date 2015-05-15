package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"head"})
public class Queue extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fields.size() == 1 && cn.fieldCount(desc("CacheableNode")) == 1 &&
                !cn.interfaces.contains("java/lang/Iterable");
    }

    @Override
    public void visit() {
        add("head", cn.getField(null, desc("CacheableNode")), literalDesc("CacheableNode"));
    }
}