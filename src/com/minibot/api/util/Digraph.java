/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.util;

import java.util.*;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public class Digraph<V, E> extends HashMap<V, Set<E>> implements Iterable<V> {

    @Override
    public final Iterator<V> iterator() {
        return super.keySet().iterator();
    }

    public final boolean containsVertex(V vertex) {
        return super.containsKey(vertex);
    }

    public final boolean containsEdge(V vertex, E edge) {
        return super.containsKey(vertex) && super.get(vertex).contains(edge);
    }

    public final boolean addVertex(V vertex) {
        if (super.containsKey(vertex))
            return false;
        super.put(vertex, new HashSet<>());
        return true;
    }

    public final void addEdge(V vertex, E edge) {
        if (!super.containsKey(vertex))
            return;
        super.get(vertex).add(edge);
    }

    public final void removeEdge(V vertex, E edge) {
        if (!super.containsKey(vertex))
            return;
        super.get(vertex).remove(edge);
    }

    public final Set<E> getEdgesOf(V vertex) {
        return Collections.unmodifiableSet(super.get(vertex));
    }

    public final void merge(Digraph<V, E> graph) {
        super.putAll(graph);
    }
}


