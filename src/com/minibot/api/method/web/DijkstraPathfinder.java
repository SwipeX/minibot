/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method.web;

import java.util.*;
import java.util.stream.Collectors;

public class DijkstraPathfinder {

    private final Set<WebVertex> unsettled, settled;
    private final Map<WebVertex, WebVertex> preds;
    private final Map<WebVertex, Integer> distances;

    public DijkstraPathfinder() {
        this.unsettled = settled = new HashSet<>();
        this.preds = new HashMap<>();
        this.distances = new HashMap<>();
    }

    public WebVertex[] generate(WebVertex src, WebVertex dest) {
        if (src.index() == dest.index())
            return new WebVertex[0];
        long time = System.currentTimeMillis();
        settled.clear();
        unsettled.clear();
        distances.clear();
        preds.clear();
        distances.put(src, 0);
        unsettled.add(src);
        while (unsettled.size() > 0) {
            WebVertex top = nearestOf(unsettled);
            settled.add(top);
            unsettled.remove(top);
            List<WebVertex> edges = unsettledEdgesFor(top);
            for (WebVertex target : edges) {
                if (shortestDistance(target) > shortestDistance(top) + distanceFor(top, target)) {
                    distances.put(target, shortestDistance(top) + distanceFor(top, target));
                    preds.put(target, top);
                    unsettled.add(target);
                }
            }
        }
        List<WebVertex> path = new ArrayList<>();
        WebVertex step = dest;
        if (preds.get(step) == null)
            return null;
        path.add(step);
        while (preds.get(step) != null) {
            step = preds.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        System.out.println((System.currentTimeMillis() - time) + "ms to generate path");
        return path.toArray(new WebVertex[path.size()]);
    }

    private int distanceFor(WebVertex node, WebVertex target) {
        for (WebVertex edge : node.edges()) {
            if (edge.equals(target)) {
                return edge.getTile().distance(node.getTile());
            }
        }
        throw new RuntimeException("WTF");
    }

    private List<WebVertex> unsettledEdgesFor(WebVertex node) {
        return node.edges().stream().filter(edge -> !settled.contains(edge)).collect(Collectors.toList());
    }

    private WebVertex nearestOf(Set<WebVertex> vertexes) {
        WebVertex closest = null;
        for (WebVertex vertex : vertexes) {
            if (closest == null || shortestDistance(vertex) < shortestDistance(closest)) {
                closest = vertex;
            }
        }
        return closest;
    }

    private int shortestDistance(WebVertex dest) {
        Integer d = distances.get(dest);
        return d == null ? Integer.MAX_VALUE : d;
    }
}
