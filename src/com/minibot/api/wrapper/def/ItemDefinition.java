package com.minibot.api.wrapper.def;

import com.minibot.internal.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/6/15.
 */
public class ItemDefinition {

    public static int id(Object raw) {
        return ModScript.hook("ItemDefinition#id").getInt(raw);
    }

    public static String name(Object raw) {
        return ModScript.hook("ItemDefinition#name").getString(raw);
    }
}
