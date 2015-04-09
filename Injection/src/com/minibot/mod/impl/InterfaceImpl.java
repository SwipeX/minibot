package com.minibot.mod.impl;

import com.minibot.mod.ModScript;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 05:46
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class InterfaceImpl implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            String def = ModScript.getDefinedName(cn.name);
            if (def != null)
                cn.interfaces.add("com/minibot/client/natives/RS" + def);
        }
    }
}
