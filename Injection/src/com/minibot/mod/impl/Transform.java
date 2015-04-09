package com.minibot.mod.impl;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 05:31
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface Transform extends Opcodes {
    void inject(Map<String, ClassNode> classes);
}
