/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.util;

import com.minibot.client.natives.RSClient;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSNpcDefinition;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;

import java.util.HashMap;
import java.util.Map;

public class DefinitionLoader {

    private static final String NULL = "null";

    private static final Map<Integer, RSObjectDefinition> OBJECT_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, RSNpcDefinition> NPC_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, RSItemDefinition> ITEM_DEFINITIONS = new HashMap<>();

    public static RSObjectDefinition findObjectDefinition(int id) {
        return OBJECT_DEFINITIONS.get(id);
    }

    public static RSNpcDefinition findNpcDefinition(int id) {
        return NPC_DEFINITIONS.get(id);
    }

    public static RSItemDefinition findItemDefinition(int id) {
        return ITEM_DEFINITIONS.get(id);
    }

    public static void loadDefinitions(RSClient client) {
        long start = System.nanoTime();
        loadObjectDefinitions(client);
        long end = System.nanoTime();
        System.out.println(String.format("loaded %s object definitions in %.2f seconds", OBJECT_DEFINITIONS.size(),
                (end - start) / 1e9));
        start = System.nanoTime();
        loadNpcDefinitions(client);
        end = System.nanoTime();
        System.out.println(String.format("loaded %s npc definitions in %.2f seconds", NPC_DEFINITIONS.size(),
                (end - start) / 1e9));
        start = System.nanoTime();
        loadItemDefinitions(client);
        end = System.nanoTime();
        System.out.println(String.format("loaded %s item definitions in %.2f seconds", ITEM_DEFINITIONS.size(),
                (end - start) / 1e9));
    }

    private static boolean loadObjectDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadObjectDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSObjectDefinition> data = new HashMap<>();
            for (int i = 0; i < 30000; i++) {
                RSObjectDefinition raw = client.loadObjectDefinition(i);
                if (raw != null) {
                    RSObjectDefinition transformed = raw.transform();
                    if (transformed != null) {
                        data.put(transformed.getId(), raw);
                    } else {
                        data.put(raw.getId(), raw);
                    }
                }
            }
            OBJECT_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean loadNpcDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadNpcDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSNpcDefinition> data = new HashMap<>();
            for (int i = 0; i < 20000; i++) {
                RSNpcDefinition raw = client.loadNpcDefinition(i);
                if (raw != null) {
                    RSNpcDefinition transformed = raw.transform();
                    if (transformed != null) {
                        data.put(transformed.getId(), raw);
                    } else {
                        data.put(raw.getId(), raw);
                    }
                }
            }
            NPC_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause().printStackTrace();
            return false;
        }
    }

    private static boolean loadItemDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadItemDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSItemDefinition> data = new HashMap<>();
            for (int i = 0; i < 20000; i++) {
                RSItemDefinition raw = client.loadItemDefinition(i);
                if (raw != null) {
                    String name = raw.getName();
                    if (name != null && !name.equals(NULL))
                        data.put(i, raw);
                }
            }
            ITEM_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
