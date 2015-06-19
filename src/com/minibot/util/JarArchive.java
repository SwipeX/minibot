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
    private Manifest manifest;

    public JarArchive(File file) {
        this.file = file;
    }

    private byte[] inputToBytes(InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public Map<String, ClassNode> build() {
        if (nodes.size() > 0)
            return nodes;
        try (final JarFile jf = new JarFile(file)) {
            final JarInputStream in = new JarInputStream(new FileInputStream(file));
            manifest = in.getManifest();
            for (JarEntry entry = in.getNextJarEntry(); entry != null; entry = in.getNextJarEntry()) {
                final String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    final ClassReader cr = new ClassReader(jf.getInputStream(entry));
                    final ClassNode cs = new ClassNode();
                    cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                    nodes.put(entryName.replace(".class", ""), cs);
                } else {
                    resources.put(entryName, inputToBytes(jf.getInputStream(entry)));
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }
}
