package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.projection.Projection;

import java.awt.*;
import java.util.Objects;

/**
 * @author Tyler Sedlar
 */
public class Tile implements Locatable {

    private int x, y, plane;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Tile(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int plane() {
        return plane;
    }

    public int localX() {
        return x() - Game.baseX();
    }

    public int localY() {
        return y() - Game.baseY();
    }

    public int fineX() {
        return localX() << 7;
    }

    public int fineY() {
        return localY() << 7;
    }

    public Point point(double dx, double dy, int height) {
        int x = localX() << 7;
        int y = localY() << 7;
        x += 128 * dx;
        y += 128 * dy;
        return Projection.toScreen(x, y, height);
    }

    public Tile derive(int x, int y) {
        return new Tile(this.x + x, this.y + y);
    }

    public void draw(Graphics2D g) {
        Polygon polygon = new Polygon();
        Point[] points = {point(0, 0, 0), point(1, 0, 0), point(1, 1, 0), point(0, 1, 0)};
        for (Point p : points) {
            if (p == null)
                return;
            polygon.addPoint(p.x, p.y);
        }
        g.draw(polygon);
    }

    @Override
    public Tile location() {
        return this;
    }

    @Override
    public int distance(Locatable locatable) {
        return locatable.distance(this);
    }

    @Override
    public int distance() {
        return Players.local().distance(this);
    }

    public int hashCode() {
        return Objects.hash(x, y, plane);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile t = (Tile) obj;
            return t.x == x && t.y == y && t.plane == plane;
        }
        return false;
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + plane;
    }
}
