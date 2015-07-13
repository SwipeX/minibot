package com.minibot.util;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * @author Tyler Sedlar
 */
public class JarArchive {

    private final Map<String, ClassNode> nodes = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();

    private final File file;

    public JarArchive(File file) {
        this.file = file;
    }

    private byte[] inputToBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public Map<String, ClassNode> build() {
        if (!nodes.isEmpty()) {
            return nodes;
        }
        try (final JarFile jf = new JarFile(file)) {
            JarInputStream in = new JarInputStream(new FileInputStream(file));
            Manifest manifest = in.getManifest();
            for (JarEntry entry = in.getNextJarEntry(); entry != null; entry = in.getNextJarEntry()) {
                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    ClassReader cr = new ClassReader(jf.getInputStream(entry));
                    ClassNode cs = new ClassNode();
                    cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                    nodes.put(entryName.replace(".class", ""), cs);
                } else {
                    resources.put(entryName, inputToBytes(jf.getInputStream(entry)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }
}