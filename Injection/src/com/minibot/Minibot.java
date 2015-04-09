package com.minibot;

import com.minibot.client.GameCanvas;
import com.minibot.client.natives.RSClient;
import com.minibot.mod.Injector;
import com.minibot.mod.ModScript;
import com.minibot.mod.impl.*;
import com.minibot.util.JarArchive;
import com.minibot.util.RSClassLoader;
import com.minibot.util.io.Crawler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 * Run with noverify enabled
 */
public class Minibot extends JFrame implements Runnable {

    private static Minibot instance;
    private final Crawler crawler;
    private RSClient client;

    public Minibot() {
        super("Minibot");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.crawler = new Crawler(Crawler.GameType.OSRS);
        new Thread(this).start();
    }

    public static Minibot instance() {
        return instance;
    }

    public static void main(String[] args) {
        instance = new Minibot();
    }

    public RSClient client() {
        return client;
    }

    public GameCanvas canvas() {
        return (GameCanvas) client.getCanvas();
    }

    @Override
    public void run() {
        crawler.crawl();
        if (crawler.outdated()) {
            crawler.download(() -> System.out.println("Downloaded: " + crawler.percent + "%"));
        }

        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.modscript)), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript");
        }

        Injector injector = new Injector(new JarArchive(new File(crawler.pack)));
        injector.getTransforms().add(new ProcessActionCallback());
        injector.getTransforms().add(new ProcessActionInvoker());
        injector.getTransforms().add(new InterfaceImpl());
        injector.getTransforms().add(new ModelHack());
        injector.getTransforms().add(new CanvasHack());
        injector.getTransforms().add(new WidgetPositionHack());
        injector.getTransforms().add(new GetterAdder());
        Map<String, byte[]> classes = injector.inject();

        RSClassLoader classloader;
        try {
            classloader = new RSClassLoader(classes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct classloader");
        }
        ModScript.setClassLoader(classloader);

        Container container = getContentPane();
        container.setBackground(Color.BLACK);
        this.client = (RSClient) crawler.applet(classloader);
        container.add(client.asApplet());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
