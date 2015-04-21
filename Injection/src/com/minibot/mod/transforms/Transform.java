package com.minibot.mod.transforms;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public interface Transform extends Opcodes {

    String PACKAGE = "com/minibot/client/natives/";

    void inject(Map<String, ClassNode> classes);
}
