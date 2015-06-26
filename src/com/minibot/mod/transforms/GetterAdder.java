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
            ClassNode where = classes.get(hook.isStatic() ? "client" : hook.getClazz());
            if (where == null)
                continue;
            String retDesc = hook.getFieldDesc().replace("[", "").replace(";", "");
            if (retDesc.startsWith("L") && !retDesc.contains("java") && !retDesc.contains("/")) {
                String prebuild = "";
                for (char c : hook.getFieldDesc().toCharArray()) {
                    if (c == '[') {
                        prebuild += '[';
                    }
                }
                retDesc = retDesc.replace("L", "");
                retDesc = prebuild + "L" + PACKAGE + "RS" + ModScript.getDefinedName(retDesc) + ";";
            } else {
                retDesc = hook.getFieldDesc();
            }
            if (retDesc.contains("null")) {
                retDesc = hook.getFieldDesc();
            }
            where.methods.add(ASMFactory.createGetter(hook, retDesc));
        }
    }
}