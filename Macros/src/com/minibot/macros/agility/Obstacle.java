package com.minibot.macros.agility;

import com.minibot.api.wrapper.locatable.Tile;

public class Obstacle {

    private final int id;

    private final String action;
    private final Tile start;
    private final Tile finish;

    public Obstacle(int id, String action, Tile start, Tile finish) {
        this.id = id;
        this.action = action;
        this.start = start;
        this.finish = finish;
    }

    public int id() {
        return id;
    }

    public String action() {
        return action;
    }

    public Tile start() {
        return start;
    }

    public Tile finish() {
        return finish;
    }
}