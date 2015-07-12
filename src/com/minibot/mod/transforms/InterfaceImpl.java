package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class InterfaceImpl implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            String def = ModScript.getDefinedName(cn.name);
            if (def != null) {
                cn.interfaces.add("com/minibot/client/natives/RS" + def);
            }
        }
    }
}