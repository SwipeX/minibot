package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.FieldHook;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class HoveredRegionTileSetter implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        FieldHook xHook = ModScript.getFieldHook("Client#hoveredRegionTileX");
        FieldHook yHook = ModScript.getFieldHook("Client#hoveredRegionTileY");
        if (xHook == null || yHook == null)
            throw new RuntimeException("hook broke?");

        MethodNode xSetter = new MethodNode(ACC_PUBLIC, "setHoveredRegionTileX", "(I)V", null, null);
        xSetter.instructions.add(new VarInsnNode(ILOAD, 1));
        xSetter.instructions.add(new FieldInsnNode(PUTSTATIC, xHook.clazz, xHook.field, xHook.fieldDesc));
        xSetter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(xSetter);

        MethodNode ySetter = new MethodNode(ACC_PUBLIC, "setHoveredRegionTileY", "(I)V", null, null);
        ySetter.instructions.add(new VarInsnNode(ILOAD, 1));
        ySetter.instructions.add(new FieldInsnNode(PUTSTATIC, yHook.clazz, yHook.field, yHook.fieldDesc));
        ySetter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(ySetter);
    }
}
