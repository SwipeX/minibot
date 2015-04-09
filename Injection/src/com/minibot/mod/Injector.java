package com.minibot.mod;

import com.minibot.mod.impl.Transform;
import com.minibot.util.JarArchive;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.*;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 05:32
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class Injector {

    private final JarArchive arch;
    private final List<Transform> transforms;

    public Injector(final JarArchive arch) {
        this.arch = arch;
        this.transforms = new ArrayList<>();
    }

    public Map<String, byte[]> inject() {
        getTransforms().forEach(t -> t.inject(arch.build()));
        Map<String, byte[]> out = new HashMap<>();
        for (ClassNode cn : arch.build().values()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(writer);
            out.put(cn.name, writer.toByteArray());
        }
        return out;
    }

    public List<Transform> getTransforms() {
        return transforms;
    }
}
