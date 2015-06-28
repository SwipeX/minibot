package com.minibot.macros.agility;

import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.Tile;

public class Obstacle {

    private final int id;

    private final String action;
    private final Area area;
    private final Tile at;

    public Obstacle(int id, String action, Area area, Tile at) {
        this.id = id;
        this.action = action;
        this.area = area;
        this.at = at;
    }

    public int id() {
        return id;
    }

    public String action() {
        return action;
    }

    public Area area() {
        return area;
    }

    public Tile at() {
        return at;
    }
}