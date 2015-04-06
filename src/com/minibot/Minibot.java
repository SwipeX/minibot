package com.minibot;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.internal.def.DefinitionLoader;
import com.minibot.internal.ext.RSCanvas;
import com.minibot.internal.mod.ModScript;
import com.minibot.internal.mod.reflection.FieldValue;
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
public class Minibot extends JFrame implements Runnable, Renderable {

    private static Minibot instance;

    public static Minibot instance() {
        return instance;
    }

    private final Crawler crawler;
    private RSCanvas canvas;

    public Minibot() {
        super("minibot");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.crawler = new Crawler(Crawler.GameType.OSRS);
        new Thread(this).start();
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString("Minibot", 16, 20);
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
        Container container = getContentPane();
        container.setBackground(Color.BLACK);
        Applet applet = crawler.applet(classloader);
        container.add(applet);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        while (canvas == null) {
            Component child = null;
            if (applet.getComponentCount() > 0)
                child = applet.getComponent(0);
            if (child != null && child.getMouseListeners().length > 0 && child.getMouseWheelListeners().length > 0 &&
                    child instanceof Canvas) {
                if (canvas == null) {
                    FieldValue fh = ModScript.hook("Client#canvas");
                    if (fh != null && fh.valid()) {
                        fh.set((canvas = new RSCanvas(container, (Canvas) child) {
                            public void render(Graphics2D g) {
                                instance.render(g);
                            }
                        }));
                    }
                }
            }
            Time.sleep(50);
        }
        while (Game.state() < Game.STATE_CREDENTIALS)
            Time.sleep(100);
        DefinitionLoader.loadDefinitions();
        canvas.original.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                System.out.println("srs..");
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
                    if (Bank.viewing()) {
                        Item iron = Bank.findByFilter(i -> {
                            String name = i.name();
                            return name != null && name.equals("Iron ore");
                        });
                        if (iron != null)
                            iron.doAction(ActionOpcodes.WIDGET_ACTION, "Withdraw-1");
                    } else {
                        Npc banker = Npcs.nearest("Banker");
                        if (banker != null) {
                            banker.doAction(ActionOpcodes.NPC_ACTION_2, "Bank");
                        }
                    }
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_2) {
                    new Thread(() -> {
                        System.out.println("starting 1337 copper powerminer");
                        while (true) {
                            if (Widgets.validate(15269890 >> 16)) {
                                Widgets.get(15269890 >> 16, 15269890 & 0xfff).doAction("Continue");
                            } else if (Inventory.count() != 0) {
                                for (final Item item : Inventory.items()) {
                                    final Point p = item.screen();
                                    if (p == null)
                                        continue;
                                    //arg1 = item index, arg2 = widget uid, opcode = 37 (item action),
                                    //arg0 = item id, action = Drop, name = Copper ore
                                    RuneScape.doAction(item.index(), 9764864, 37, 436, "Drop", "Copper ore", p.x, p.y);
                                }
                            } else if (Players.local() != null && Players.local().animation() == -1) {
                                final Point p = Projection.toScreen(53 << 7, 49 << 7);
                                if (p == null)
                                    continue;
                                RuneScape.doAction(53, 49, 3, 1294129333, "Mine", "Rocks", p.x, p.y);
                            }
                            Time.sleep(2000);
                        }
                    }).start();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_3) {
                    new Thread(() -> {
                        System.out.println("starting 1337 cow killer");
                        while (true) {
                            if (Widgets.validate(15269890 >> 16)) {
                                Widgets.get(15269890 >> 16, 15269890 & 0xfff).doAction("Continue");
                            } else if (Players.local() != null && Players.local().interactingIndex() == -1) {
                                final Npc npc = Npcs.nearestByFilter(n -> {
                                    final String name = n.name();
                                    return name != null && n.interactingIndex() == -1
                                            && (name.equals("Cow") || name.equals("Cow calf"));
                                });
                                if (npc == null)
                                    continue;
                                npc.doAction(ActionOpcodes.NPC_ACTION_1, "Attack");
                            }
                            Game.resetMouseIdleTime();
                            Time.sleep(2000);
                        }
                    }).start();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_4) {
                    System.out.println("cmon..");
                    Npcs.loaded();
                }
            }
        });
    }

    public static void main(String[] args) {
        instance = new Minibot();
    }
}
