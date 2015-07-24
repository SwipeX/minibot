package org.objectweb.asm.commons.wrapper;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Tyler Sedlar
 * @since 3/8/15.
 */
public class ClassField implements Opcodes {

    public final ClassFactory owner;
    public final FieldNode field;

    public ClassField(ClassFactory owner, FieldNode field) {
        this.owner = owner;
        this.field = field;
    }

    public String name() {
        return field.name;
    }

    public String desc() {
        return field.desc;
    }

    public int access() {
        return field.access;
    }

    public String key() {
        return owner.name() + "." + name();
    }

    public boolean local() {
        return (access() & ACC_STATIC) == 0;
    }
}
