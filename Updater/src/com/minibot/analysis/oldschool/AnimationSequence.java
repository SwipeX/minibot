package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {})
public class AnimationSequence extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheableNode")) && cn.getFieldTypeCount() == 3 && cn.fieldCount("Z") == 1;
    }

    @Override
    public void visit() {
    }
}
