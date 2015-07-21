package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.projection.Projection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Objects;

/**
 * @author Tyler Sedlar
 */
public class Tile implements Locatable {

    private final int x;
    private final int y;
    private final int plane;

    public Tile(int x, int y) {
        this(x, y, 0);
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

    public Point toViewport(double dx, double dy, int height) {
        int x = localX() << 7;
        int y = localY() << 7;
        x += 128 * dx;
        y += 128 * dy;
        return Projection.groundToViewport(x, y, height);
    }

    public Tile derive(int x, int y) {
        return new Tile(this.x + x, this.y + y);
    }

    public void draw(Graphics2D g) {
        Polygon polygon = new Polygon();
        Point[] points = {toViewport(0, 0, 0), toViewport(1, 0, 0), toViewport(1, 1, 0), toViewport(0, 1, 0)};
        for (Point p : points) {
            if (p == null) {
                return;
            }
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
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return Players.local().distance(this);
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