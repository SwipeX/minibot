package com.minibot.macros.agility;

public class Course {

    private final boolean marks;

    private final Obstacle[] obstacles;

    public Course(boolean marks, Obstacle... obstacles) {

        this.marks = marks;
        this.obstacles = obstacles;
    }

    public boolean marks() {
        return marks;
    }

    public Obstacle[] obstacles() {
        return obstacles;
    }
}