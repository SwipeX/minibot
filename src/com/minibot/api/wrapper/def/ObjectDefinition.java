package com.minibot.api.wrapper.def;

import com.minibot.internal.mod.ModScript;

/**
 * @author Tyler Sedlar
 */
public class ObjectDefinition {

    public static int id(Object raw) {
        return ModScript.hook("ObjectDefinition#id").getInt(raw);
    }
    
    public static String name(Object raw) {
        return ModScript.hook("ObjectDefinition#name").getString(raw);
    }

    public static String[] actions(Object raw) {
        String[] actions = (String[]) ModScript.hook("ObjectDefinition#actions").get(raw);
        return actions != null ? actions : new String[0];
    }

    public static int[] transformIds(Object raw) {
        int[] transformIds = (int[]) ModScript.hook("ObjectDefinition#transformIds").get(raw);
        return transformIds != null ? transformIds : new int[0];
    }

    public static int transformIndex(Object raw) {
        return ModScript.hook("ObjectDefinition#transformIndex").getInt(raw);
    }
}
