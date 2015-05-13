package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.natives.RSNpc;

import java.util.ArrayList;
import java.util.List;

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

    public static Npc nearest(Filter<Npc> filter) {
        int dist = Integer.MAX_VALUE;
        Npc temp = null;
        for (Npc npc : loaded()) {
            if (filter.accept(npc)) {
                int d = npc.distance();
                if (d < dist) {
                    dist = d;
                    temp = npc;
                }
            }
        }
        return temp;
    }

    public static Npc nearest(String name) {
        return nearest(npc -> npc != null && npc.name().equals(name));
    }
}
