package com.minibot.api.wrapper.node;

import com.minibot.internal.mod.ModScript;

/**
 * @author Tyler Sedlar
 */
public class RSNode  {

    public static long uid(Object raw) {
        return ModScript.hook("Node#uid").getLong(raw);
    }

    public static int widgetId(Object raw) {
        return ModScript.hook("WidgetNode#id").getInt(raw);
    }

    public static Object previous(Object raw) {
        return ModScript.hook("Node#previous").get(raw);
    }

    public static Object next(Object raw) {
        return ModScript.hook("Node#next").get(raw);
    }
}
