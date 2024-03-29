package com.minibot.util.io;

import com.minibot.util.Configuration;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;

public class Crawler {

    private final Map<String, String> parameters = new HashMap<>();
    private final GameType type;
    private final String pack;
    private final String modscript;
    private final String home, config;
    private int percent;

    private int hash = -1;

    public Crawler(GameType type) {
        this.type = type;
        pack = Configuration.CACHE + (type == GameType.OSRS ? "os" : "rs3") + "_pack.jar";
        modscript = Configuration.CACHE + (type == GameType.OSRS ? "oldschool" : "modern") + ".dat";
        home = "http://oldschool6.runescape.com/";
        config = home + "jav_config.ws";
    }

    public Applet start(ClassLoader classloader, JFrame container) {
        try {
            String main = parameters.get("initial_class").replace(".class", "");
            Applet applet = (Applet) classloader.loadClass(main).newInstance();
            applet.setBackground(Color.BLACK);
            applet.setPreferredSize(getAppletSize());
            applet.setLayout(null);
            applet.setStub(getEnvironment(applet));
            applet.setVisible(true);
            return applet;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AppletStub getEnvironment(Applet applet) {

        return new AppletStub() {
            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public URL getDocumentBase() {
                try {
                    return new URL(parameters.get("codebase"));
                } catch (MalformedURLException e) {
                    return null;
                }
            }

            @Override
            public URL getCodeBase() {
                try {
                    return new URL(parameters.get("codebase"));
                } catch (MalformedURLException e) {
                    return null;
                }
            }

            @Override
            public String getParameter(String name) {
                return parameters.get(name);
            }

            @Override
            public void appletResize(int width, int height) {
                Dimension size = new Dimension(width, height);
                applet.setSize(size);
            }

            @Override
            public AppletContext getAppletContext() {
                return null;
            }
        };
    }

    private int getLocalHash() {
        try {
            URL url = new File(pack).toURI().toURL();
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (MalformedURLException e) {
            return -1;
        }
    }

    public int getHash() {
        return hash;
    }

    public int getRemoteHash() {
        try {
            URL url = new URL(home + parameters.get("initial_jar"));
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (IOException e) {
            return -1;
        }
    }

    public void initHash() {
        hash = getLocalHash();
    }

    public boolean isOutdated() {
        File gamepack = new File(pack);
        if (!gamepack.exists()) {
            return true;
        }
        if (hash == -1) {
            hash = getLocalHash();
        }
        boolean outdated = hash == -1 || hash != getRemoteHash();
        if (!outdated) {
            percent = 100;
        }
        return outdated;
    }

    public boolean crawl() {
        try {
            List<String> source = Internet.read(config);
            for (String line : source) {
                if (line.startsWith("param=")) {
                    line = line.substring(6);
                }
                int idx = line.indexOf("=");
                if (idx == -1) {
                    continue;
                }
                parameters.put(line.substring(0, idx), line.substring(idx + 1));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean download(String target, Runnable callback) {
        hash = getRemoteHash();
        return Internet.download(home + parameters.get("initial_jar"), target, new InternetCallback() {
            @Override
            public void onDownload(int p) {
                percent = p;
                if (callback != null) {
                    callback.run();
                }
            }
        }) != null;
    }

    public boolean download(String target) {
        return download(target, null);
    }

    public boolean download() {
        return download(pack);
    }

    public boolean download(Runnable callback) {
        return download(pack, callback);
    }

    public Dimension getAppletSize() {
        try {
            return new Dimension(Integer.parseInt(parameters.get("applet_minwidth")),
                    Integer.parseInt(parameters.get("applet_minheight")));
        } catch (NumberFormatException e) {
            return new Dimension(765, 503);
        }
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public GameType getType() {
        return type;
    }

    public String getPack() {
        return pack;
    }

    public String getModscript() {
        return modscript;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public enum GameType {
        OSRS,
        RS3
    }
}