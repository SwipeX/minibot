package com.minibot.api.wrapper.def;

import com.minibot.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class NpcDefinition {

    public static int id(Object raw) {
        return ModScript.hook("NpcDefinition#id").getInt(raw);
    }

    public static String name(Object raw) {
        return ModScript.hook("NpcDefinition#name").getString(raw);
    }

    public static String[] actions(Object raw) {
        String[] actions = (String[]) ModScript.hook("NpcDefinition#actions").get(raw);
        return actions != null ? actions : new String[0];
    }

    public static int[] transformIds(Object raw) {
        int[] transformIds = (int[]) ModScript.hook("NpcDefinition#transformIds").get(raw);
        return transformIds != null ? transformIds : new int[0];
    }

    public static int transformIndex(Object raw) {
        return ModScript.hook("NpcDefinition#transformIndex").getInt(raw);
    }
}
