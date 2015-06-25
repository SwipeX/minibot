package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class Objects {

    public static GameObject[] allAt(Tile tile) {
        return allAt(tile.localX(), tile.localY(), tile.plane());
    }

    public static GameObject[] allAt(int rx, int ry, int z) {
        if (rx > 104 || rx < 0 || ry > 104 || ry < 0 || z < 0 || z > 3)
            return new GameObject[0];
        List<GameObject> objs = new ArrayList<>();
        RSTile[][][] tiles = Minibot.instance().client().getRegion().getTiles();
        if (tiles == null)
            return new GameObject[0];
        RSTile tile = tiles[z][rx][ry];
        if (tile == null)
            return new GameObject[0];
        RSInteractableObject[] entities = tile.getObjects();
        if (entities != null && entities.length > 0) {
            for (RSInteractableObject entity : entities) {
                if (entity == null)
                    continue;
                objs.add(new GameObject(entity));
            }
        }
        RSWallDecoration wall = tile.getWallDecoration();
        if (wall != null)
            objs.add(new GameObject(wall));
        RSFloorDecoration floor = tile.getFloorDecoration();
        if (floor != null)
            objs.add(new GameObject(floor));
        RSBoundary boundary = tile.getBoundary();
        if (boundary != null)
            objs.add(new GameObject(boundary));
        return objs.toArray(new GameObject[objs.size()]);
    }

    public static GameObject topAt(Tile t) {
        GameObject[] all = allAt(t);
        return all.length == 0 ? null : all[0];
    }

    public static Deque<GameObject> loaded(int radius) {
        Deque<GameObject> objects = new ArrayDeque<>();
        int bx = Game.baseX();
        int by = Game.baseY();
        int plane = Game.plane();
        for (int x = bx; x < bx + 104; x++) {
            for (int y = by; y < by + 104; y++) {
                Tile t = new Tile(x, y, plane);
                if (radius == -1 || t.distance() < radius)
                    Collections.addAll(objects, allAt(t.localX(), t.localY(), t.plane()));
            }
        }
        return objects;
    }

    public static GameObject nearestByFilter(Filter<GameObject> filter, int radius) {
        Deque<GameObject> loaded = loaded(radius);
        List<GameObject> filtered = loaded.stream().filter(filter::accept).collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            Collections.sort(filtered, (o1, o2) -> o1.distance() - o2.distance());
            return filtered.get(0);
        }
        return null;
    }

    public static GameObject nearestByFilter(Filter<GameObject> filter) {
        return nearestByFilter(filter, -1);
    }

    public static GameObject nearestByAction(String action, int radius) {
        return nearestByFilter(o -> {
            RSObjectDefinition def = o.definition();
            if (def != null) {
                String[] actions = def.getActions();
                return actions != null && Action.indexOf(actions, action) >= 0;
            }
            return false;
        }, radius);
    }

    public static GameObject nearestByAction(String action) {
        return nearestByAction(action, -1);
    }

    public static GameObject nearestByName(String name, int radius) {
        return nearestByFilter(o -> {
            RSObjectDefinition def = o.definition();
            if (def != null) {
                String objName = def.getName();
                return objName != null && objName.equals(name);
            }
            return false;
        }, radius);
    }

    public static GameObject nearestByName(String name) {
        return nearestByName(name, -1);
    }
}
