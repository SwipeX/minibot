package com.minibot;

import com.minibot.mod.ModScript;
import com.minibot.util.JarArchive;
import com.minibot.util.RSClassLoader;
import com.minibot.util.io.Crawler;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Minibot extends JFrame implements Runnable {

    private static Minibot instance;

    public static Minibot instance() {
        return instance;
    }

    private final Crawler crawler;

    public Minibot() {
        super("com/minibot");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.crawler = new Crawler(Crawler.GameType.OSRS);
        new Thread(this).start();
    }

    @Override
    public void run() {
        crawler.crawl();
        if (crawler.outdated()) {
            crawler.download(() -> {
                System.out.println("Downloaded: " + crawler.percent + "%");
            });
        }

        /** Inject callback **/
        JarArchive arch = new JarArchive(new File(crawler.pack));
        for (ClassNode cn : arch.build().values()) {
            if (!cn.name.equals("bx"))
                continue;
            for (MethodNode mn : cn.methods) {
                if (!mn.name.equals("ba") || !mn.desc.equals("(IIIILjava/lang/String;Ljava/lang/String;III)V"))
                    continue;
                final InsnList stack = new InsnList();
                stack.add(new VarInsnNode(Opcodes.ILOAD, 0));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 1));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 2));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 3));
                stack.add(new VarInsnNode(Opcodes.ALOAD, 4));
                stack.add(new VarInsnNode(Opcodes.ALOAD, 5));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 6));
                stack.add(new VarInsnNode(Opcodes.ILOAD, 7));
                stack.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Callback.class.getName().replace('.', '/'), "doAction",
                        "(IIIILjava/lang/String;Ljava/lang/String;II)V"));
                mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
                System.out.println("...Injected!");
            }
        }
        Map<String, byte[]> classes = new HashMap<>();
        /** save jar **/
        for (ClassNode cn : arch.build().values()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(writer);
            classes.put(cn.name, writer.toByteArray());
        }

        RSClassLoader classloader;
        try {
            classloader = new RSClassLoader(classes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct classloader");
        }
        ModScript.setClassLoader(classloader);

/*        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.modscript)), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript");
        }*/
        Container container = getContentPane();
        container.setBackground(Color.BLACK);
        Applet applet = crawler.applet(classloader);
        container.add(applet);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        instance = new Minibot();
    }
}
