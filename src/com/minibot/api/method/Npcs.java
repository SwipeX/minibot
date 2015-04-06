package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.util.Filter;
import com.minibot.api.wrapper.def.NpcDefinition;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.internal.mod.ModScript;

import java.util.Arrays;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Npcs {

    public static Npc[] loaded() {
        Object[] raws = (Object[]) ModScript.hook("Client#npcs").get();
        if (raws == null || raws.length == 0)
            return new Npc[0];
        int[] indices = (int[]) ModScript.hook("Client#npcIndices").get();
        if (indices == null || indices.length == 0)
            return new Npc[0];
        Npc[] array = new Npc[0];
        for (int index : indices) {
            Object npc = raws[index];
            if (npc == null)
                continue;
            Npc internal = new Npc(npc, index);
            if (internal.valid())
                array = Array.add(array, internal);
        }
        return array;
    }

    public static Npc[] findByFilter(int dist, Filter<Npc> filter) {
        Npc[] npcs = new Npc[0];
        for (Npc npc : loaded()) {
            if (npc == null)
                continue;
            if (dist != -1 && npc.distance() > dist)
                continue;
            if (filter.accept(npc))
                npcs = Array.add(npcs, npc);
        }
        return npcs;
    }

    public static Npc[] findByFilter(Filter<Npc> filter) {
        return findByFilter(-1, filter);
    }

    public static Npc nearestByFilter(int dist, Filter<Npc> filter) {
        Npc[] loaded = findByFilter(dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    public static Npc nearestByFilter(Filter<Npc> filter) {
        return nearestByFilter(-1, filter);
    }

    public static Npc nearest(String name) {
        return nearestByFilter(npc -> {
            NpcDefinition def = npc.definition();
            if (def == null)
                return false;
            String npcName = def.name();
            return npcName != null && npcName.equals(name);
        });
    }
}
