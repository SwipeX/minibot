package com.minibot.api.method.projection;

import com.minibot.internal.mod.ModScript;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tyler Sedlar
 */
public class Rendering {

    public static byte[][][] rules() {
        return (byte[][][]) ModScript.hook("Client#renderRules").get();
    }

    public static int[][][] heights() {
        return (int[][][]) ModScript.hook("Client#tileHeights").get();
    }

    public static Point[] points(Shape shape) {
        List<Point> points = new LinkedList<>();
        Rectangle bounds = shape.getBounds();
        for (int x = bounds.x; x < bounds.getMaxX(); x++) {
            for (int y = bounds.y; y < bounds.getMaxY(); y++) {
                if (shape.contains(x, y))
                    points.add(new Point(x, y));
            }
        }
        return points.toArray(new Point[points.size()]);
    }
}
