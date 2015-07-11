package com.minibot;

import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.breaks.BreakHandler;
import com.minibot.bot.farm.Connection;
import com.minibot.bot.macro.Macro;
import com.minibot.client.GameCanvas;
import com.minibot.client.natives.RSClient;
import com.minibot.mod.Injector;
import com.minibot.mod.ModScript;
import com.minibot.mod.transforms.*;
import com.minibot.ui.GameMenu;
import com.minibot.ui.MacroSelector;
import com.minibot.util.Configuration;
import com.minibot.util.DefinitionLoader;
import com.minibot.util.JarArchive;
import com.minibot.util.RSClassLoader;
import com.minibot.util.io.Crawler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private static Connection connection;

    public Minibot() {
        super("Minibot");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        crawler = new Crawler(Crawler.GameType.OSRS);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        connection = new Connection(Connection.DEFAULT_IRC);
        addCloseListener();
        new Thread(this).start();
    }

    /**
     * Well this should be improved, the data is rather inconsistent and likely should be tested.
     */
    private void addCloseListener() {
        Frame[] frames = Frame.getFrames();
        for (Frame f : frames) {
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Macro current = MacroSelector.current();
                    if (current != null) {
                        Player local = Players.local();
                        String name = local != null ? local.name() : Minibot.instance().client().getUsername();
                        connection().script(1, name, current.getClass().getSimpleName());
                    }
                }
            });
        }
    }

    public static Connection connection() {
        return connection;
    }

    public static Minibot instance() {
        return instance;
    }

    public static void main(String... args) {
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
            crawler.download(() -> System.out.println("Downloaded: " + crawler.getPercent() + "%"));
        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.getModscript())), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript");
        }
        File randomDat = new File(System.getProperty("user.home") + "/random.dat");
        if (randomDat.exists())
            randomDat.setReadOnly();
        Injector injector = new Injector(new JarArchive(new File(crawler.getPack())));
        injector.getTransforms().add(new ProcessActionCallback());
        injector.getTransforms().add(new ProcessActionInvoker());
        injector.getTransforms().add(new InterfaceImpl());
        injector.getTransforms().add(new ModelHack());
        injector.getTransforms().add(new CanvasHack());
        injector.getTransforms().add(new GetterAdder());
        injector.getTransforms().add(new DefinitionInvoker());
        injector.getTransforms().add(new HoveredRegionTileSetter());
        injector.getTransforms().add(new MiscSetters());
        injector.getTransforms().add(new WidgetHack());
        injector.getTransforms().add(new ChatboxCallback());
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
        client = (RSClient) crawler.start(classloader);
        container.add(client.asApplet());
        container.add(GameMenu.component(), BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        while (Game.state() < Game.STATE_CREDENTIALS)
            Time.sleep(100);
        DefinitionLoader.loadDefinitions(client);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Game.playing())
                    return;
                System.out.println(Players.local().location());
                Time.sleep(2000);
            }
        }).start();
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