package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"tiles", "objects"})
public class Region extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getAbnormalFieldCount() == 2 &&
                cn.fieldCount("[[[" + desc("Tile")) == 1 && cn.fieldCount("[" + desc("InteractableObject")) == 1;
    }

    @Override
    public void visit() {
        add("tiles", getCn().getField(null, "[[[" + desc("Tile")), "[[[" + literalDesc("Tile"));
        add("objects", getCn().getField(null, "[" + desc("InteractableObject")), "[" + literalDesc("InteractableObject"));
    }
}