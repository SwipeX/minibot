package com.minibot.util;

import java.io.File;

/**
 * @author Tyler Sedlar
 */
public enum OperatingSystem {

    WINDOWS, MAC, LINUX, UNKNOWN;

    public static String getHomeDirectory() {
        switch (get()) {
            case WINDOWS: {
                return System.getProperty("user.home") + File.separator;
            }
            default: {
                return "/home/";
            }
        }
    }

    @Override
    public String toString() {
        String orig = super.toString();
        return Character.toUpperCase(orig.charAt(0)) + orig.substring(1).toLowerCase();
    }

    public boolean is64BitJVM() {
        String[] keys = {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        for (String key : keys) {
            String property = System.getProperty(key);
            if (property != null && property.contains("64")) {
                return true;
            }
        }
        return false;
    }

    public String getUserAgentPart() {
        switch (OperatingSystem.get()) {
            case LINUX: {
                return "X11; Linux " + (is64BitJVM() ? "x86_64" : "i686");
            }
            case MAC: {
                return "Macintosh; Intel Mac OS X 10_7_5";
            }
            default: {
                return "Windows NT 6.1" + (is64BitJVM() ? "; WOW64" : "");
            }
        }
    }

    public static OperatingSystem get() {
        String os = System.getProperty("os.name");
        for (OperatingSystem o : OperatingSystem.values()) {
            if (os.contains(o.toString()))
                return o;
        }
        return UNKNOWN;
    }
}