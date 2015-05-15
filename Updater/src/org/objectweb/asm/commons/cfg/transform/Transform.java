package org.objectweb.asm.commons.cfg.transform;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * @author Tyler Sedlar
 */
public abstract class Transform {

    public abstract void transform(Map<String, ClassNode> classes);
}
