package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {})
public class Cache extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 4 && cn.fieldCount(desc("CacheableNode")) == 1 &&
                cn.fieldCount(desc("HashTable")) == 1;
    }

    @Override
    public void visit() {
    }
}
