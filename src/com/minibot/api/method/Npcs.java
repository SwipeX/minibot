package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.natives.RSNpc;
import com.minibot.client.natives.RSNpcDefinition;

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

    public static Npc[] loaded(int radius) {
        RSNpc[] raws = raw();
        if (raws == null || raws.length == 0) {
            return new Npc[0];
        }
        int[] indices = Minibot.instance().client().getNpcIndices();
        if (indices == null || indices.length == 0) {
            return new Npc[0];
        }
        List<Npc> npcs = new ArrayList<>(indices.length);
        for (int index : indices) {
            RSNpc raw = raws[index];
            if (raw == null) {
                continue;
            }
            Npc npc;
            try {
                npc = new Npc(raw, index);
            } catch (IllegalStateException e) {
                npc = null;
            }
            if (npc != null) {
                if (radius == -1 || npc.distance() <= radius) {
                    npcs.add(npc);
                }
            }
        }
        return npcs.toArray(new Npc[npcs.size()]);
    }

    public static Npc[] loaded() {
        return loaded(-1);
    }

    public static Npc atIndex(int idx) {
        RSNpc[] raws = raw();
        if (raws == null || raws.length == 0 || idx < 0 || idx >= raws.length) {
            return null;
        }
        RSNpc raw = raws[idx];
        return raw == null ? null : new Npc(raw, idx);
    }

    public static Npc nearestByFilter(Filter<Npc> filter, int radius) {
        int dist = Integer.MAX_VALUE;
        Npc temp = null;
        for (Npc npc : loaded(radius)) {
            if (npc.name() != null && filter.accept(npc)) {
                int d = npc.distance();
                if (d < dist) {
                    dist = d;
                    temp = npc;
                }
            }
        }
        return temp;
    }

    public static Npc nearestByFilter(Filter<Npc> filter) {
        return nearestByFilter(filter, -1);
    }

    public static Npc nearestByAction(String action, int radius) {
        return nearestByFilter(o -> {
            RSNpcDefinition def = o.definition();
            if (def != null) {
                String[] actions = def.getActions();
                return actions != null && Action.indexOf(actions, action) >= 0;
            }
            return false;
        }, radius);
    }

    public static Npc nearestByAction(String action) {
        return nearestByAction(action, -1);
    }

    public static Npc nearestByName(String name, int radius) {
        return nearestByFilter(o -> {
            RSNpcDefinition def = o.definition();
            if (def != null) {
                String objName = def.getName();
                return objName != null && objName.equals(name);
            }
            return false;
        }, radius);
    }

    public static Npc nearestByName(String name) {
        return nearestByName(name, -1);
    }
}