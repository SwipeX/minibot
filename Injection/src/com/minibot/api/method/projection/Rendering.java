package com.minibot.api.method.projection;

import com.minibot.Minibot;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tyler Sedlar
 */
public class Rendering {

    public static byte[][][] rules() {
        return Minibot.instance().client().getRenderRules();
    }

    public static int[][][] heights() {
        return Minibot.instance().client().getTileHeights();
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
