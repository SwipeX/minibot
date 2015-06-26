package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {})
public class Canvas extends GraphVisitor {

    @Override
    public String iface() {
        return getUpdater().getAccessorPackage() + "/input/Canvas";
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas") && cn.fieldCount("Ljava/awt/Component;") == 1;
    }

    @Override
    public void visit() {
    }
}