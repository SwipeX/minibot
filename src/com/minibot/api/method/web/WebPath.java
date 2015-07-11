/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method.web;

import com.minibot.api.method.*;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Path;
import com.minibot.api.wrapper.locatable.*;

import java.util.Iterator;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public class WebPath implements Path {

    private final WebVertex src, dest;
    private final WebVertex[] vertices;

    private WebPath(WebVertex src, WebVertex dest) {
        this.src = src;
        this.dest = dest;
        if (src == null || dest == null)
            throw new RuntimeException("Failed to find vertices!");
        WebVertex[] path = Walking.web().pathfinder().generate(src, dest);
        if (path == null)
            throw new RuntimeException("Failed to find path!");
        this.vertices = path;
    }

    public static WebPath build(int srcIdx, int destIdx) {
        return new WebPath(Walking.web().vertexAt(srcIdx), Walking.web().vertexAt(destIdx));
    }

    public static WebPath build(int destIdx) {
        WebVertex v = Walking.web().nearestVertex();
        return v == null ? null : build(v.index(), destIdx);
    }

    public static WebPath build(Locatable src, Locatable dest) {
        return new WebPath(Walking.web().nearestVertexTo(src), Walking.web().nearestVertexTo(dest));
    }

    public static WebPath build(Locatable dest) {
        return build(Players.local(), dest);
    }

    @Override
    public Tile[] toArray() {
        Tile[] tiles = new Tile[vertices.length];
        int i = 0;
        for (WebVertex vertex : vertices) {
            tiles[i] = vertex.getTile();
            i++;
        }
        return tiles;
    }

    @Override //TODO absolutely disgusting
    public boolean step(Option... options) {
        if (dest.getTile().distance(src.getTile()) <= 3)
            return true;
        for (Option option : options)
            option.handle();
        WebVertex next = next();
        if (next != null) {
            if (next instanceof ObjectVertex) {
                ObjectVertex vertex = (ObjectVertex) next;
                GameObject object;
                if (vertex.name() != null) {
                    if (vertex.action() != null) {
                        object = Objects.nearestByFilter(t -> t.location().distance(vertex.getTile()) <= 2 && vertex.name().equals(t.name()) && t.containsAction(vertex.action()));
                    } else {
                        object = Objects.nearestByFilter(t -> t.location().distance(vertex.getTile()) <= 2 && vertex.name().equals(t.name()));
                    }
                } else {
                    object = Objects.nearestByFilter(t -> t.location().distance(vertex.getTile()) <= 2 && t.name() != null);
                }

                if (object == null) {
                    throw new IllegalStateException("Bad ObjectVertex (" + vertex.index() + ") on web?");
                }

                if (vertex.action() == null) {
                    object.processAction(vertex.action());
                } else {
                    String[] actions = object.definition().getActions();
                    if (actions != null && actions.length > 0) {
                        object.processAction(actions[0]); //guess
                    }
                }

            } else {
                Walking.walkTo(next.getTile());
            }
            //TODO hook queueSize and add a moving check
            return Time.sleep(() -> Players.local().animation() == -1, 5000);
        }
        return false;
    }

    public WebVertex next() {
        if (vertices.length == 0)
            return null;
        if (vertices.length == 1)
            return vertices[0];
        int dist = Integer.MAX_VALUE;
        int nearIndex = -1;
        for (int i = 0; i < vertices.length; i++) {
            int temp = vertices[i].getTile().distance(src.getTile());
            if (temp < dist) {
                dist = temp;
                nearIndex = i;
            }
        }
        return nearIndex == vertices.length - 1 ? dest : vertices[nearIndex + 1];
    }

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            @Override
            public boolean hasNext() {
                return WebPath.this.next() != null;
            }

            @Override
            public Tile next() {
                return WebPath.this.next().getTile();
            }
        };
    }
}

