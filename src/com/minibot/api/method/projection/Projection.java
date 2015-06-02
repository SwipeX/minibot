package com.minibot.api.method.projection;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.*;

import java.awt.*;
import java.util.LinkedList;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Projection {

    public static int[] SIN_TABLE = new int[2048];
    public static int[] COS_TABLE = new int[2048];

    static {
        for (int i = 0; i < SIN_TABLE.length; i++) {
            SIN_TABLE[i] = (int) (65536.0D * Math.sin((double) i * 0.0030679615D));
            COS_TABLE[i] = (int) (65536.0D * Math.cos((double) i * 0.0030679615D));
        }
    }

    public static boolean onViewport(int x, int y) {
        return x > 1 && x < 516 && y > 1 && y < 337;
    }

    public static boolean onViewport(Point p) {
        return onViewport(p.x, p.y);
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        int xd = x2 - x1;
        int yd = y2 - y1;
        return Math.sqrt(xd * xd + yd * yd);
    }

    public static double distance(Point p1, Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }

    public static double distance(Locatable l1, Locatable l2) {
        if (l1 == null || l2 == null)
            return Double.NaN;
        Tile t1 = l1.location();
        Tile t2 = l2.location();
        if (t1 == null || t2 == null)
            return Double.NaN;
        return distance(t1.x(), t1.y(), t2.x(), t2.y());
    }

    public static Point groundToViewport(int x, int y) {
        try {
            if (x >= 128 && x <= 13056 && y >= 128 && y <= 13056) {
                int z = getGroundHeight(x, y);
                x -= Camera.x();
                y -= Camera.y();
                z -= Camera.z();
                int pitchSin = SIN_TABLE[Camera.pitch()];
                int pitchCos = COS_TABLE[Camera.pitch()];
                int yawSin = SIN_TABLE[Camera.yaw()];
                int yawCos = COS_TABLE[Camera.yaw()];
                int angle = y * yawSin + x * yawCos >> 16;
                y = y * yawCos - x * yawSin >> 16;
                x = angle;
                angle = z * pitchCos - y * pitchSin >> 16;
                y = z * pitchSin + y * pitchCos >> 16;
                if (y == 0)
                    return new Point(-1, -1);
                z = angle;
                return new Point((x << 9) / y + 256, (z << 9) / y + 167);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

    public static Point groundToViewport(int x, int y, int height) {
        if (x >= 128 && x <= 13056 && y >= 128 && y <= 13056) {
            int pitch = Camera.pitch();
            if (pitch < 0)
                return null;
            int yaw = Camera.yaw();
            if (yaw < 0)
                return null;
            int z = getGroundHeight(x, y) - height;
            x -= Camera.x();
            y -= Camera.y();
            z -= Camera.z();
            int pitchSin = SIN_TABLE[pitch];
            int pitchCos = COS_TABLE[pitch];
            int yawSin = SIN_TABLE[yaw];
            int yawCos = COS_TABLE[yaw];
            int angle = y * yawSin + x * yawCos >> 16;
            y = y * yawCos - x * yawSin >> 16;
            x = angle;
            angle = z * pitchCos - y * pitchSin >> 16;
            y = z * pitchSin + y * pitchCos >> 16;
            if (y == 0)
                return null;
            z = angle;
            return new Point((x << 9) / y + 256, (z << 9) / y + 167);
        }
        return null;
    }

    public static Point locatableToMap(Locatable locatable) {
        return groundToMap(locatable.location().x(), locatable.location().y());
    }

    public static Point groundToMap(int x, int y, boolean ignoreDist) {
        WidgetComponent minimap = Minimap.component();
        if (minimap == null)
            return null;
        Player local = Players.local();
        if (local.raw() == null)
            return null;
        if (ignoreDist || distance(local, new Tile(x, y)) < 17) {
            x -= Game.baseX();
            y -= Game.baseY();
            int calcX = (x << 2) - (local.fineX() >> 5) + 2;
            int calcY = (y << 2) - (local.fineY() >> 5) + 2;
            int degree = Minimap.scale() + Minimap.rotation() & 0x7FF;
            int offset = Minimap.offset();
            int sin = SIN_TABLE[degree] * 256 / (offset + 256);
            int cos = COS_TABLE[degree] * 256 / (offset + 256);
            int centerX = (calcY * sin) + (calcX * cos) >> 16;
            int centerY = (calcX * sin) - (calcY * cos) >> 16;
            int screenX = 12 + (minimap.x() + (minimap.width() / 2)) + centerX;
            int screenY = 1 + (minimap.y() + (minimap.height() / 2)) + centerY; //TODO re-add widget x and y if needed
            return new Point(screenX, screenY);
        }
        return null;
    }

    public static Point groundToMap(int x, int y) {
        return groundToMap(x, y, false);
    }

    public static int getGroundHeight(int x, int y) {
        int x1 = x >> 7;
        int y1 = y >> 7;
        if (x1 < 0 || x1 > 103 || y1 < 0 || y1 > 103)
            return 0;
        byte[][][] rules = renderRules();
        if (rules == null)
            return 0;
        int[][][] heights = tileHeights();
        if (heights == null)
            return 0;
        int plane = Game.plane();
        if (plane < 3 && (rules[1][x1][y1] & 0x2) == 2)
            plane++;
        int x2 = x & 0x7F;
        int y2 = y & 0x7F;
        int h1 = heights[plane][x1][y1] * (128 - x2) + heights[plane][x1 + 1][y1] * x2 >> 7;
        int h2 = heights[plane][x1][y1 + 1] * (128 - x2) + heights[plane][x1 + 1][y1 + 1] * x2 >> 7;
        return h1 * (128 - y2) + h2 * y2 >> 7;
    }

    public static Point locatableToViewport(Locatable locatable) {
        return groundToViewport(locatable.location().fineX(), locatable.location().fineY());
    }

    public static byte[][][] renderRules() {
        return Minibot.instance().client().getRenderRules();
    }

    public static int[][][] tileHeights() {
        return Minibot.instance().client().getTileHeights();
    }

    public static Point[] getPointsIn(Shape shape) {
        java.util.List<Point> points = new LinkedList<>();
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
