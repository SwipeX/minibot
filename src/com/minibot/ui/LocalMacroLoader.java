package com.minibot.ui;

import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.MacroDefinition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
 
/**
 * @author Tyler Sedlar
 * @since 4/30/2015
 */
public class LocalMacroLoader extends MacroLoader<File> {
 
    private final List<MacroDefinition> definitions = new ArrayList<>();
 
    @Override
    public void parse(File root) throws IOException, ClassNotFoundException {
        definitions.clear();
        Stack<File> files = new Stack<>();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        files.push(root);
        while (!files.isEmpty()) {
            File file = files.pop();
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null)
                    Collections.addAll(files, subFiles);
            } else {
                if (file.getName().endsWith(".class")) {
                    readClass(classLoader, root, file);
                } else if (file.getName().endsWith(".jar")) {
                    readJar(file);
                }
            }
        }
    }
 
    @SuppressWarnings("unchecked")
    private void readClass(ClassLoader classLoader, File root, File file) throws ClassNotFoundException {
        String className = file.getPath();
        String rootPath = root.getPath();
        className = className.substring(rootPath.length() + 1);
        className = className.substring(0, className.length() - ".class".length());
        className = className.replace(File.separatorChar, '.');
        Class<?> c = classLoader.loadClass(className);
        if (accept(c))
            definitions.add(new MacroDefinition((Class<? extends Macro>) c));
    }
 
    @SuppressWarnings("unchecked")
    private void readJar(File file) throws IOException, ClassNotFoundException {
        JarFile jar = new JarFile(file);
        URLClassLoader ucl = new URLClassLoader(new URL[]{file.toURI().toURL()});
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                String name = entry.getName();
                name = name.substring(0, name.length() - ".class".length());
                name = name.replace('/', '.');
                Class<?> c = ucl.loadClass(name);
                if (accept(c))
                    definitions.add(new MacroDefinition((Class<? extends Macro>) c));
            }
        }
    }
 
    @Override
    public MacroDefinition[] definitions() {
        return definitions.toArray(new MacroDefinition[definitions.size()]);
    }
}