package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.Array;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.natives.RSNpc;

import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Npcs {

    public static RSNpc[] raw() {
        return Minibot.instance().client().getNpcs();
    }

    public static Npc[] loaded() {
        RSNpc[] raws = raw();
        if (raws == null || raws.length == 0)
            return new Npc[0];
        int[] indices = Minibot.instance().client().getNpcIndices();
        if (indices == null || indices.length == 0)
            return new Npc[0];
        List<Npc> npcs = new ArrayList<>(indices.length);
        for (int index : indices) {
            RSNpc raw = raws[index];
            if (raw == null)
                continue;
            Npc npc = new Npc(raw, index);
            npcs.add(npc);
        }
        return npcs.toArray(new Npc[npcs.size()]);
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
            String npcName = npc.name();
            return npcName != null && npcName.equals(name);
        });
    }
}
