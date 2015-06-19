package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.client.natives.RSItem;
import com.minibot.client.natives.RSNode;
import com.minibot.client.natives.RSNodeDeque;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class Ground {

    public static Deque<GroundItem> loaded() {
        Deque<GroundItem> items = new ArrayDeque<>();
        for (int i = 0; i < 104; i++) {
            for (int j = 0; j < 104; j++) {
                Deque<GroundItem> temp = at(i,j);
                if (temp == null)
                    continue;
                items.addAll(temp);
            }
        }
        return items;
    }

    public static Deque<GroundItem> loaded(int radius) {
        if (radius == -1)
            return loaded();
        Deque<GroundItem> items = new ArrayDeque<>();
        int xx = Players.local().localX();
        int yy = Players.local().localY();
        for (int i = xx - radius; i < xx + radius; i++) {
            for (int j = yy - radius; j < yy + radius; j++) {
                Deque<GroundItem> temp = at(i, j);
                if (temp == null)
                    continue;
                items.addAll(temp);
            }
        }
        return items;
    }

    public static Deque<GroundItem> at(int x, int y) {
        int baseX = Game.baseX(), baseY = Game.baseY();
        RSNodeDeque[][][] raw = raw();
        if (raw == null || raw.length == 0)
            return null;
        RSNodeDeque rawDeque;
        try {
            rawDeque = raw[Game.plane()][x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        if (rawDeque == null)
            return null;
        Deque<GroundItem> items = new ArrayDeque<>();
        RSNode tail = rawDeque.getTail();
        RSNode current = tail.getPrevious();
        while (current != null && current != tail) {
            if (current instanceof RSItem)
                items.add(new GroundItem(current, x + baseX, y + baseY));
            current = current.getPrevious();
        }
        return items;
    }

    private static RSNodeDeque[][][] raw() {
        return Minibot.instance().client().getGroundItems();
    }

    public static Deque<GroundItem> findByFilter(int dist, Filter<GroundItem> filter) {
        return loaded(dist).stream().filter(obj -> obj != null && filter.accept(obj)).collect(
                Collectors.toCollection(ArrayDeque::new)
        );
    }

    public static Deque<GroundItem> findByFilter(Filter<GroundItem> filter) {
        return findByFilter(15, filter);
    }

    public static GroundItem nearestByFilter(int dist, Filter<GroundItem> filter) {
        Deque<GroundItem> loaded = findByFilter(dist, filter);
        if (loaded.size() == 0)
            return null;
        int distance = Integer.MAX_VALUE;
        GroundItem nearest = null;
        for (GroundItem item : loaded) {
            int localDistance = item.distance();
            if (localDistance < distance) {
                nearest = item;
                distance = localDistance;
            }
        }
        return nearest;
    }

    public static GroundItem nearestByFilter(Filter<GroundItem> filter) {
        return nearestByFilter(-1, filter);
    }
}
