/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method.web;

import com.minibot.api.wrapper.locatable.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public class WebVertex {

    private final int index, x, y, plane;
    private final List<WebVertex> edges;
    private final int[] edgeIndexes;

    public WebVertex(int index, int x, int y, int plane, int[] edgeIndexes) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.edgeIndexes = edgeIndexes;
        this.edges = new ArrayList<>();
    }

    public WebVertex(int index, int x, int y, int plane) {
        this(index, x, y, 0, new int[0]);
    }

    public WebVertex(int index, int x, int y, int[] edgeIndexes) {
        this(index, x, y, 0, edgeIndexes);
    }

    public WebVertex(int index, int x, int y) {
        this(index, x, y, new int[0]);
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

    public Tile getTile() {
        return new Tile(x, y, plane);
    }

    @Override
    public int hashCode() {
        return plane << 0x1c | y << 0xe | x;
    }

    public int index() {
        return index;
    }

    public List<WebVertex> edges() {
        return edges;
    }

    public int[] edgeIndices() {
        return edgeIndexes;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WebVertex && ((WebVertex) o).index() == index;
    }
}

