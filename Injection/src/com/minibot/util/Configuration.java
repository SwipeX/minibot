package com.minibot.util;

import com.minibot.Minibot;

import java.io.File;

/**
 * m
 *
 * @author Tyler Sedlar
 */
public class Configuration {

    public static final String APPLICATION_NAME = "com/minibot";

    public static final String HOME = getSystemHome() + File.separator + APPLICATION_NAME + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String DATA = CACHE + "data" + File.separator;
    public static final String[] DIRECTORIES = {CACHE, DATA};


    public static boolean isLocal() {
        return !Minibot.class.getResource(Minibot.class.getSimpleName() + ".class").toString().contains("jar:");
    }

    public static void setup() {
        for (String dir : DIRECTORIES)
            new File(dir).mkdirs();
    }

    public static String getSystemHome() {
        if (OperatingSystem.get() == OperatingSystem.WINDOWS) {
            return System.getProperty("user.home") + "/Documents/";
        } else {
            return System.getProperty("user.home") + "/";
        }
    }
}
