package com.minibot;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.GameCanvas;
import com.minibot.client.natives.RSClient;
import com.minibot.mod.Injector;
import com.minibot.mod.ModScript;
import com.minibot.mod.transforms.*;
import com.minibot.util.*;
import com.minibot.util.io.Crawler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
        return (GameCanvas) client.asApplet().getComponent(0);
    }

    @Override
    public void run() {
        crawler.crawl();
        if (crawler.isOutdated()) {
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
        injector.getTransforms().add(new DefinitionInvoker());
        injector.getTransforms().add(new HoveredRegionTileSetter());
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
        this.client = (RSClient) crawler.start(classloader);
        container.add(client.asApplet());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        while (Game.state() < Game.STATE_CREDENTIALS)
            Time.sleep(100);
        DefinitionLoader.loadDefinitions(client);
        canvas().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
                    if (Bank.viewing()) {
                        Item iron = Bank.first(i -> {
                            String name = i.name();
                            return name != null && name.equals("Iron ore");
                        });
                        if (iron != null)
                            iron.processAction(ActionOpcodes.WIDGET_ACTION, "Withdraw-1");
                    } else {
                        Npc banker = Npcs.nearest("Banker");
                        if (banker != null) {
                            banker.processAction("Bank");
                        }
                    }
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_2) {
                    new Thread(() -> {
                        System.out.println("starting 1337 copper powerminer");
                        while (true) {
                            if (Widgets.validate(15269890 >> 16)) {
                                // Widgets.get(15269890 >> 16, 15269890 & 0xfff).processAction("Continue");
                            } else if (Inventory.count() != 0) {
                                for (final Item item : Inventory.items()) {
                                    final Point p = item.point();
                                    if (p == null)
                                        continue;
                                    item.processAction(ActionOpcodes.ITEM_ACTION_1, "Drop");
                                }
                            } else if (Players.local() != null && Players.local().animation() == -1) {
                                final Point p = Projection.groundToViewport(53 << 7, 49 << 7);
                                if (p == null)
                                    continue;
                                RuneScape.processAction(53, 49, 3, 1294129333, "Mine", "Rocks", p.x, p.y);
                            }
                            Time.sleep(2000);
                        }
                    }).start();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_3) {
                    new Thread(() -> {
                        System.out.println("starting 1337 cow killer");
                        while (true) {
                            if (Players.local() != null && Players.local().targetIndex() == -1) {
                                Npc npc = Npcs.nearest(n -> {
                                    if (n.maxHealth() > 0 && n.health() <= 0)
                                        return false;
                                    String name = n.name();
                                    return name != null && n.targetIndex() == -1
                                            && (name.equals("Cow") || name.equals("Cow calf"));
                                });
                                if (npc == null)
                                    continue;
                                npc.processAction("Attack");
                            }
                            Time.sleep(2000);
                        }
                    }).start();
                }
            }
        });
    }
}
