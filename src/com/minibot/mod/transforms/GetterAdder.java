package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.util.ASMFactory;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class GetterAdder implements Transform {
    @Override
    public void inject(Map<String, ClassNode> classes) {
        //yuck redo this l8r
        for (FieldHook hook : ModScript.FIELD_HOOK_MAP.values()) {
            ClassNode where = classes.get(hook.isStatic ? "client" : hook.clazz);
            if (where == null)
                continue;
            String retDesc = hook.fieldDesc.replace("[", "").replace(";", "");
            if (retDesc.startsWith("L") && !retDesc.contains("java") && !retDesc.contains("/")) {
                String prebuild = "";
                for (char c : hook.fieldDesc.toCharArray()) {
                    if (c == '[') {
                        prebuild += '[';
                    }
                }
                retDesc = retDesc.replace("L", "");
                retDesc = prebuild + "L" + PACKAGE + "RS" + ModScript.getDefinedName(retDesc) + ";";
            } else {
                retDesc = hook.fieldDesc;
            }
            if (retDesc.contains("null")) {
                retDesc = hook.fieldDesc;
            }
            where.methods.add(ASMFactory.createGetter(hook, retDesc));
        }
    }
}
