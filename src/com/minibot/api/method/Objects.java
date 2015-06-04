package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSInteractableObject;
import com.minibot.client.natives.RSTile;

import java.util.*;

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
        return objs.toArray(new GameObject[objs.size()]);
    }

    private static GameObject[] copy(RSInteractableObject[] array) {
        if (array == null || array.length == 0) return null;
        GameObject[] objects = new GameObject[array.length];
        for (int i = 0; i < array.length; i++) {
            RSInteractableObject obj = array[i];
            if (obj != null)
                objects[i] = new GameObject(obj);
        }
        return objects;
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

    public static GameObject topAt(Tile t) {
        GameObject[] all = allAt(t);
        return all.length == 0 ? null : all[0];
    }
}
