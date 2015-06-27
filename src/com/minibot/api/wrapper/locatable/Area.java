package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.projection.Projection;

public class Area implements Locatable {

    private final Tile from;
    private final Tile to;
    private final Tile center;

    // sw to ne
    public Area(Tile from, Tile to) {
        this.from = from;
        this.to = to;
        center = new Tile((from.x() + to.x()) / 2, (from.y() + to.y()) / 2, from.plane());
    }

    public Tile from() {
        return from;
    }

    public Tile to() {
        return to;
    }

    public boolean contains(Locatable locatable) {
        if (locatable != null) {
            Tile tile = locatable.location();
            return tile != null && tile.x() >= from.x() && tile.y() >= from.y() && tile.x() <= to.x() && tile.y() <= to.y();
        }
        return false;
    }

    @Override
    public Tile location() {
        return center;
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return distance(center);
    }
}