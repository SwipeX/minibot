package org.objectweb.asm.commons.wrapper;

import org.objectweb.asm.commons.util.Filter;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Tyler Sedlar
 * @since 3/8/15.
 */
public class ClassFactory {

    public final ClassNode node;
    public ClassField[] fields;
    public ClassMethod[] methods;

    public ClassFactory(ClassNode node) {
        this.node = node;
        this.fields = new ClassField[node.fields.size()];
        for (int i = 0; i < this.fields.length; i++) {
            this.fields[i] = new ClassField(this, node.fields.get(i));
        }
        this.methods = new ClassMethod[node.methods.size()];
        for (int i = 0; i < this.methods.length; i++) {
            this.methods[i] = new ClassMethod(this, node.methods.get(i));
        }
    }

    public String name() {
        return node.name;
    }

    public String desc() {
        return "L" + name() + ";";
    }

    public String superName() {
        return node.superName;
    }

    public boolean ownerless() {
        return superName().equals("java/lang/Object");
    }

    public List<String> interfaces() {
        return node.interfaces;
    }

    public int access() {
        return node.access;
    }

    public void remove(ClassMethod method) {
        if (!node.methods.contains(method.method)) {
            return;
        }
        node.methods.remove(method.method);
        ClassMethod[] methods = new ClassMethod[this.methods.length - 1];
        int idx = 0;
        for (ClassMethod cm : this.methods) {
            if (cm.equals(method)) {
                continue;
            }
            methods[idx++] = cm;
        }
        this.methods = methods;
    }

    public ClassField findField(Filter<ClassField> filter) {
        for (ClassField field : fields) {
            if (filter.accept(field)) {
                return field;
            }
        }
        return null;
    }

    public List<ClassField> findFields(Filter<ClassField> filter) {
        List<ClassField> valid = new LinkedList<>();
        for (ClassField field : fields) {
            if (filter.accept(field)) {
                valid.add(field);
            }
        }
        return valid;
    }

    public int fieldCount(String desc, boolean includeStatic) {
        return findFields(f -> {
            if (f.local() || (!f.local() && includeStatic)) {
                if (desc == null || f.desc().equals(desc)) {
                    return true;
                }
            }
            return false;
        }).size();
    }

    public int fieldCount(String desc) {
        return fieldCount(desc, false);
    }

    public int fieldCount() {
        return fieldCount(null, false);
    }

    public int fieldTypeCount(boolean includeStatic) {
        Set<String> descs = new HashSet<>();
        return findFields(f -> {
            if (f.local() || (!f.local() && includeStatic)) {
                if (!descs.contains(f.desc())) {
                    descs.add(f.desc());
                    return true;
                }
            }
            return false;
        }).size();
    }

    public int fieldTypeCount() {
        return fieldTypeCount(false);
    }

    public int abnormalFieldCount(boolean includeStatic) {
        return findFields(f -> {
            if (f.local() || (!f.local() && includeStatic)) {
                if (f.desc().contains("L") && f.desc().endsWith(";") && !f.desc().contains("java")) {
                    return true;
                }
            }
            return false;
        }).size();
    }

    public int abnormalFieldCount() {
        return abnormalFieldCount(false);
    }

    public ClassMethod findMethod(Filter<ClassMethod> filter) {
        for (ClassMethod method : methods) {
            if (filter.accept(method)) {
                return method;
            }
        }
        return null;
    }

    public List<ClassMethod> findMethods(Filter<ClassMethod> filter) {
        List<ClassMethod> valid = new LinkedList<>();
        for (ClassMethod method : methods) {
            if (filter.accept(method)) {
                valid.add(method);
            }
        }
        return valid;
    }

    public int methodCount(String desc, boolean includeStatic) {
        return findMethods(m -> {
            if (m.local() || (!m.local() && includeStatic)) {
                if (desc == null || m.desc().equals(desc)) {
                    return true;
                }
            }
            return false;
        }).size();
    }

    public int methodCount(String desc) {
        return methodCount(desc, false);
    }

    public int methodCount() {
        return methodCount(null, false);
    }

    public int methodTypeCount(boolean includeStatic) {
        Set<String> descs = new HashSet<>();
        return findMethods(m -> {
            if (m.local() || (!m.local() && includeStatic)) {
                if (!descs.contains(m.desc())) {
                    descs.add(m.desc());
                    return true;
                }
            }
            return false;
        }).size();
    }

    public List<String> constructors() {
        List<String> descs = new LinkedList<>();
        findMethods(m -> {
            if (m.name().equals("<init>")) {
                descs.add(m.desc());
                return true;
            }
            return false;
        });
        return descs;
    }

    public int methodTypeCount() {
        return fieldTypeCount(false);
    }
}
