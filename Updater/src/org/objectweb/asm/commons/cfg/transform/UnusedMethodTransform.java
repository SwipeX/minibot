package org.objectweb.asm.commons.cfg.transform;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.commons.wrapper.ClassMethod;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 3/19/15.
 */
public abstract class UnusedMethodTransform extends Transform {

    public abstract void populateEntryPoints(List<ClassMethod> entries);

    private final List<ClassMethod> entryPoints = new LinkedList<>();
    private final Set<String> validMethodKeys = new HashSet<>();
    private final List<ClassMethod> validMethods = new LinkedList<>();

    private int totalMethods = 0;

    public List<ClassMethod> found() {
        return validMethods;
    }

    private void follow(Map<String, ClassFactory> classes, List<ClassMethod> followed, ClassMethod method) {
        if (validMethodKeys.contains(method.key()))
            return;
        validMethodKeys.add(method.key());
        followed.add(method);
        method.method.accept(new MethodVisitor() {
            public void visitMethodInsn(MethodInsnNode min) {
                if (classes.containsKey(min.owner)) {
                    ClassFactory factory = classes.get(min.owner);
                    ClassMethod innerMethod = factory.findMethod(cm -> cm.method.name.equals(min.name) &&
                            cm.method.desc.equals(min.desc));
                    if (innerMethod != null)
                        follow(classes, followed, innerMethod);
                    if (classes.containsKey(factory.node.superName)) {
                        ClassFactory superFactory = classes.get(factory.node.superName);
                        ClassMethod superMethod = superFactory.findMethod(cm -> cm.method.name.equals(min.name) &&
                                cm.method.desc.equals(min.desc));
                        if (superMethod != null)
                            follow(classes, followed, superMethod);
                    }
                }
            }
        });
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        Map<String, ClassFactory> factories = new HashMap<>();
        for (ClassNode cn : classes.values())
            factories.put(cn.name, new ClassFactory(cn));
        populateEntryPoints(entryPoints);
        for (ClassNode cn : classes.values())
            totalMethods += cn.methods.size();
        for (ClassMethod method : entryPoints)
            follow(factories, validMethods, method);
        for (ClassFactory cf : factories.values()) {
            cf.findMethods(mf -> !validMethodKeys.contains(mf.key())).forEach(ClassMethod::remove);
        }
    }

    @Override
    public String toString() {
        int removed = totalMethods - validMethods.size();
        return removed + "/" + totalMethods + " [V: " + validMethods.size() + "]";
    }
}
