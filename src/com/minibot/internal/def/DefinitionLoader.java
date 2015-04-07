package com.minibot.internal.def;

import com.minibot.api.method.Game;
import com.minibot.api.wrapper.def.ItemDefinition;
import com.minibot.api.wrapper.def.NpcDefinition;
import com.minibot.api.wrapper.def.ObjectDefinition;
import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.InvokeHook;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class DefinitionLoader {

    private static final String NULL = "null";

    private static final Map<Integer, Object> OBJECT_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, Object> NPC_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, Object> ITEM_DEFINITIONS = new HashMap<>();

    public static Object findObjectDefinition(int id) {
        return OBJECT_DEFINITIONS.get(id);
    }

    public static Object findNpcDefinition(int id) {
        return NPC_DEFINITIONS.get(id);
    }

    public static Object findItemDefinition(int id) {
        return ITEM_DEFINITIONS.get(id);
    }

    public static void loadDefinitions() {
        long start = System.nanoTime();
        loadObjectDefinitions();
        long end = System.nanoTime();
        System.out.println(String.format("loaded %s object definitions in %.2f seconds", OBJECT_DEFINITIONS.size(),
                (end - start) / 1e9));
        start = System.nanoTime();
        loadNpcDefinitions();
        end = System.nanoTime();
        System.out.println(String.format("loaded %s npc definitions in %.2f seconds", NPC_DEFINITIONS.size(),
                (end - start) / 1e9));
        start = System.nanoTime();
        loadItemDefinitions();
        end = System.nanoTime();
        System.out.println(String.format("loaded %s item definitions in %.2f seconds", ITEM_DEFINITIONS.size(),
                (end - start) / 1e9));
    }

    private static boolean loadObjectDefinitions() {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadObjectDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, Object> data = new HashMap<>();
            for (int i = 0; i < 30000; i++) {
                Object raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{i});
                if (raw != null) {
                    String name = ObjectDefinition.name(raw);
                    if (name != null) {
                        int id = i;
                        if (name.equals(NULL)) {
                            try {
                                id = ObjectDefinition.transformIds(raw)[Game.settings()[ObjectDefinition.transformIndex(raw)]];
                            } catch (Exception e) {
                                continue;
                            }
                            raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{id});
                            if (raw == null)
                                continue;
                            name = ObjectDefinition.name(raw);
                            if (name == null || name.equals(NULL))
                                continue;
                        }
                        data.put(id, raw);
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

    private static boolean loadNpcDefinitions() {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadNpcDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, Object> data = new HashMap<>();
            for (int i = 0; i < 10000; i++) {
                Object raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{i});
                if (raw != null) {
                    String name = NpcDefinition.name(raw);
                    if (name != null) {
                        int id = i;
                        if (name.equals(NULL)) {
                            try {
                                id = NpcDefinition.transformIds(raw)[Game.settings()[NpcDefinition.transformIndex(raw)]];
                            } catch (Exception e) {
                                continue;
                            }
                            raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{id});
                            if (raw == null)
                                continue;
                            name = NpcDefinition.name(raw);
                            if (name == null || name.equals(NULL))
                                continue;
                        }
                        data.put(id, raw);
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

    private static boolean loadItemDefinitions() {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadItemDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, Object> data = new HashMap<>();
            for (int i = 0; i < 15000; i++) {
                Object raw = invoke.invokeStatic(new Class<?>[]{int.class}, new Object[]{i});
                if (raw != null) {
                    String name = ItemDefinition.name(raw);
                    if (name != null && !name.equals(NULL))
                        data.put(i, name);
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
