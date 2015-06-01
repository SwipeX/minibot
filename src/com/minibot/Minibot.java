package com.minibot;

import com.minibot.api.method.Game;
import com.minibot.api.util.Time;
import com.minibot.bot.BreakHandler;
import com.minibot.client.GameCanvas;
import com.minibot.client.natives.RSClient;
import com.minibot.mod.Injector;
import com.minibot.mod.ModScript;
import com.minibot.mod.transforms.*;
import com.minibot.ui.GameMenu;
import com.minibot.util.Configuration;
import com.minibot.util.DefinitionLoader;
import com.minibot.util.JarArchive;
import com.minibot.util.RSClassLoader;
import com.minibot.util.io.Crawler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Minibot extends JFrame implements Runnable {

    private static Minibot instance;
    private final Crawler crawler;
    private RSClient client;
    private String username;
    private String password;
    private boolean macroRunning;
    private boolean farming;
    private BreakHandler breakHandler;

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Configuration.setup();
        crawler.crawl();
        if (crawler.isOutdated())
            crawler.download(() -> System.out.println("Downloaded: " + crawler.percent + "%"));
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
        injector.getTransforms().add(new MiscSetters());
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
        container.add(GameMenu.component(), BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        while (Game.state() < Game.STATE_CREDENTIALS)
            Time.sleep(100);
        DefinitionLoader.loadDefinitions(client);
//        Macro macro = new ChinHunter();
//        canvas().addKeyListener(new KeyAdapter() {
//            public void keyPressed(KeyEvent e) {
//                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
//                    username = instance.client.getUsername();
//                    password = instance.client.getPassword();
//                    canvas().addRenderable((Renderable) macro);
//                    macro.start();
//                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_2) {
//                    canvas().removeRenderable((Renderable) macro);
//                    macro.interrupt();
//                }
//            }
//        });
    }


    public void setFarming(boolean farming) {
        this.farming = farming;
    }

    public boolean isFarming() {
        return farming;
    }

    public boolean isMacroRunning() {
        return macroRunning;
    }

    public void setMacroRunning(boolean macroRunning) {
        this.macroRunning = macroRunning;
    }

    public BreakHandler breakHandler() {
        return breakHandler;
    }
}
