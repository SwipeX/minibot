package org.objectweb.asm.commons.wrapper;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 * @since 3/8/15.
 */
public class ClassMethod implements Opcodes {

    public final ClassFactory owner;
    public final MethodNode method;
    public final Handle handle;

    public ClassMethod(ClassFactory owner, MethodNode method) {
        this.owner = owner;
        this.method = method;
        this.handle = new Handle(0, owner.node.name, method.name, method.desc);
    }

    public String name() {
        return method.name;
    }

    public String desc() {
        return method.desc;
    }

    public int access() {
        return method.access;
    }

    public boolean local() {
        return (access() & ACC_STATIC) == 0;
    }

    public InsnList instructions() {
        return method.instructions;
    }

    public String key() {
        return owner.node.name + "." + method.name + method.desc;
    }

    public void remove() {
        owner.remove(this);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ClassMethod && ((ClassMethod) o).key().equals(key())) ||
                (o instanceof MethodNode && method.equals(o));
    }
}
