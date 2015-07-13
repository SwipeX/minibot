package com.minibot.util;

import java.io.File;

/**
 * @author Tyler Sedlar
 */
public class Configuration {

    public static final String APPLICATION_NAME = "minibot";

    public static final String HOME = getSystemHome() + File.separator + APPLICATION_NAME + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String[] DIRECTORIES = {CACHE};

    public static void setup() {
        for (String dir : DIRECTORIES) {
            new File(dir).mkdirs();
        }
    }

    public static String getSystemHome() {
        return OperatingSystem.get() == OperatingSystem.WINDOWS ? System.getProperty("user.home") + "/Documents/"
                : System.getProperty("user.home") + "/";
    }
}
