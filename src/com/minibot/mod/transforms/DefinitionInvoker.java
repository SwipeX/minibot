/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.mod.transforms;

import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class DefinitionInvoker implements Transform {

    @Override
    public void inject(Map<String, ClassNode> classes) {
        String obj = ModScript.getClass("ObjectDefinition");
        String npc = ModScript.getClass("NpcDefinition");
        addTransformInvoker("NpcDefinition", classes.get(npc));
        addTransformInvoker("ObjectDefinition", classes.get(obj));
        //lame as fuck
        addGetInvoker("ObjectDefinition", classes.get("client"));
        addGetInvoker("NpcDefinition", classes.get("client"));
        addGetInvoker("ItemDefinition", classes.get("client"));
    }

    private void addTransformInvoker(String defined, ClassNode node) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "transform", "()L" + PACKAGE + "RS" + defined + ";", null, null);
        InvokeHook ih = ModScript.serveInvoke(defined + "#transform");
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        assert ih != null;
        if (ih.getPredicate() != Integer.MAX_VALUE) {
            mn.instructions.add(new LdcInsnNode(ih.getPredicate()));
        }
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, ih.getClazz(), ih.getMethod(), ih.getDesc(), false));
        mn.instructions.add(new InsnNode(ARETURN));
        node.methods.add(mn);
    }

    private void addGetInvoker(String defined, ClassNode client) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "load" + defined, "(I)L" + PACKAGE + "RS" + defined + ";", null, null);
        InvokeHook ih = ModScript.serveInvoke("Client#load" + defined);
        mn.instructions.add(new VarInsnNode(ILOAD, 1));
        assert ih != null;
        if (ih.getPredicate() != Integer.MAX_VALUE) {
            mn.instructions.add(new LdcInsnNode(ih.getPredicate()));
        }
        mn.instructions.add(new MethodInsnNode(INVOKESTATIC, ih.getClazz(), ih.getMethod(), ih.getDesc(), false));
        mn.instructions.add(new InsnNode(ARETURN));
        client.methods.add(mn);
    }
}