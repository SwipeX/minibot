package com.minibot.internal.def;

import com.minibot.internal.mod.ModScript;
import com.minibot.internal.mod.hooks.InvokeHook;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class DefinitionLoader {

    private static final String NULL = "null";

    private static final Map<Integer, String> ITEM_NAMES = new HashMap<>();

    public static String findItemName(int id) {
        return ITEM_NAMES.get(id);
    }

    public static void loadDefinitions() {
        long start = System.nanoTime();
        loadItemDefinitions();
        long end = System.nanoTime();
        System.out.println(String.format("loaded %s item definitions in %.2f seconds", ITEM_NAMES.size(),
                (end - start) / 1e9));
    }

    private static String itemName(Object raw) {
        return ModScript.hook("ItemDefinition#name").getString(raw);
    }

    private static boolean loadItemDefinitions() {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadItemDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, String> data = new HashMap<>();
            for (int i = 0; i < 15000; i++) {
                Object raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{i});
                if (raw != null) {
                    String name = itemName(raw);
                    if (name != null && !name.equals(NULL))
                        data.put(i, name);
                }
            }
            ITEM_NAMES.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
