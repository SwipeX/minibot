package com.minibot.macros.agility;

public class Course {

    private final Obstacle[] obstacles;

    public Course(Obstacle... obstacles) {
        this.obstacles = obstacles;
    }

    public Obstacle[] obstacles() {
        return obstacles;
    }
}