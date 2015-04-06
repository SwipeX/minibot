package com.minibot;

import com.minibot.api.Packet;
import com.minibot.api.method.Bank;
import com.minibot.api.method.Game;
import com.minibot.api.method.Npcs;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.internal.def.DefinitionLoader;
import com.minibot.internal.mod.ModScript;
import com.minibot.util.io.Crawler;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Minibot extends JFrame implements Runnable {

    private final Crawler crawler;

    public Minibot() {
        super("minibot");
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
        URLClassLoader classloader;
        try {
            classloader = new URLClassLoader(new URL[]{new File(crawler.pack).toURI().toURL()});
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct classloader");
        }
        ModScript.setClassLoader(classloader);
        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.modscript)), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript");
        }
        Applet applet = crawler.applet(classloader);
        add(applet);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        Component canvas = null;
        do {
            if (applet.getComponents().length > 0)
                canvas = applet.getComponent(0);
            Time.sleep(50);
        } while (canvas == null || !(canvas instanceof Canvas));
        while (Game.state() < Game.STATE_CREDENTIALS)
            Time.sleep(100);
        DefinitionLoader.loadDefinitions();
        canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
                    if (Bank.viewing()) {
                        Item iron = Bank.findByFilter(i -> {
                            String name = i.name();
                            return name != null && name.equals("Iron ore");
                        });
                        if (iron != null)
                            iron.doAction(Packet.INTERFACE, "Withdraw-9");
                    } else {
                        Npc banker = Npcs.nearest("Banker");
                        if (banker != null) {
                            banker.doAction(Packet.NPC_ACTION_2, "Bank");
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new Minibot();
    }
}
