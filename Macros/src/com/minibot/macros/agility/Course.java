package com.minibot.macros.agility;

public class Course {

    private final int radius;

    private final Obstacle[] obstacles;

    public Course(int radius, Obstacle... obstacles) {
        this.radius = radius;
        this.obstacles = obstacles;
    }

    public int radius() {
        return radius;
    }

    public Obstacle[] obstacles() {
        return obstacles;
    }
}