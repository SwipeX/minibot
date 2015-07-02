package com.minibot.macros.agility;

import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.Tile;

public class Obstacle {

    private final int id;

    private final String action;
    private final Area area;
    private final Area fail;
    private final Tile at;

    public Obstacle(int id, String action, Area area, Area fail, Tile at) {
        this.id = id;
        this.action = action;
        this.area = area;
        this.fail = fail;
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

    public Area fail() {
        return fail;
    }

    public Tile at() {
        return at;
    }
}