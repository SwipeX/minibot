package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSInteractableObject;
import com.minibot.client.natives.RSTile;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class Objects {
    public static GameObject[] allAt(Tile tile) {
        return allAt(tile, Minibot.instance().client().getRegion().getTiles());
    }

    public static GameObject[] allAt(Tile tile, RSTile[][][] tiles) {
        if (tiles == null || tiles.length == 0)
            return null;
        int x = tile.localX();
        int y = tile.localY();
        RSTile local;
        try {
            local = tiles[Game.plane()][x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return local != null ? copy(local.getObjects()) : null;
    }

    private static GameObject[] copy(RSInteractableObject[] array) {
        GameObject[] objects = new GameObject[array.length];
        if (objects == null) return null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                objects[i] = new GameObject(array[i]);
        }
        return objects;
    }

    public static Deque<GameObject> loaded(int radius) {
        Deque<GameObject> objects = new ArrayDeque<>();
        int bx = Game.baseX();
        int by = Game.baseY();
        int plane = Game.plane();
        RSTile[][][] tiles = Minibot.instance().client().getRegion().getTiles();
        for (int x = bx; x < bx + 104; x++) {
            for (int y = by; y < by + 104; y++) {
                Tile t = new Tile(x, y, plane);
                if (radius == -1 || t.distance() < radius) {
                    GameObject[] local = allAt(t, tiles);
                    if (local != null)
                        objects.addAll(Arrays.asList(local));
                }
            }
        }
        return objects;
    }

    public static GameObject topAt(Tile t) {
        GameObject[] all = allAt(t);
        return (all == null || all.length == 0) ? null : all[0];
    }
}
