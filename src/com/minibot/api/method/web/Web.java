/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method.web;

import com.minibot.api.method.Players;
import com.minibot.api.util.Digraph;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.locatable.Locatable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public class Web extends Digraph<WebVertex, WebVertex> {

    private static final DijkstraPathfinder PATHFINDER = new DijkstraPathfinder();

    public Web() {
        try {
            InputStream memerino = new FileInputStream("./web.txt");
            Scanner lordOfTheMemes = new Scanner(memerino);
            while (lordOfTheMemes.hasNextLine()) {
                String meme = lordOfTheMemes.nextLine();
                String[] kek = meme.split(" ");
                int index = Integer.parseInt(kek[0]);
                String type = kek[1];
                int x = Integer.parseInt(kek[2]), y = Integer.parseInt(kek[3]), z = Integer.parseInt(kek[4]);
                int[] edges = new int[kek.length - 5];
                for (int i = 0; i < kek.length - 5; i++) {
                    edges[i] = Integer.parseInt(kek[i + 5]);
                }
                if (type.equals("object")) {
                    String[] data = interactDataFor(index);
                    super.addVertex(new ObjectVertex(index, x, y, z, edges, data[0], data[1]));
                }
                super.addVertex(new WebVertex(index, x, y, z, edges));
            }
            memerino.close();
            for (WebVertex vertex : this) {
                for (WebVertex edge : this) {
                    for (int edgeI : vertex.edgeIndices()) {
                        if (edge.index() == edgeI) {
                            vertex.edges().add(edge);
                            super.addEdge(vertex, edge);
                        }
                    }
                }
                if (vertex.edges().size() != vertex.edgeIndices().length) {
                    System.err.println("edges of vertex " + vertex.index());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] interactDataFor(int vertex) {
        switch (vertex) {
            case 135:
                return new String[]{"Gate", "Pay-toll(10gp)"};
            case 310:
            case 805:
            case 806:
                return new String[]{"Door", "Open"};
            case 312:
                return new String[]{"Staircase", "Climb-down"};
            case 314:
                return new String[]{"Staircase", "Climb-up"};
            case 347:
            case 767:
            case 768:
                return new String[]{"Gate", "Open"};
            case 593:
            case 595:
                return new String[]{"Large door", "Open"};
            case 743:
            case 744:
                return new String[]{"Underwall tunnel", "Climb-into"};
            case 41:
            case 764:
                return new String[]{"Crumbling wall", "Climb-over"};
            case 338:
            case 814:
                return new String[]{"Trapdoor", "Climb-down"};
            case 770:
            case 807:
                return new String[]{"Ladder", "Climb-down"};
            case 336:
            case 803:
            case 808:
            case 813:
                return new String[]{"Ladder", "Climb-up"};
            case 629:
            case 700:
            case 714:
            case 809:
            case 812:
                return new String[]{"Gate", "Open"};
            case 492:
                return new String[]{"Guild door", "Open"};
            default: {
                System.out.println("UNKNOWN OBJECT FOR VERTEX " + vertex);
                return new String[2];
            }
        }
    }

    public WebVertex vertexAt(int idx) {
        for (WebVertex v : this) {
            if (v.index() == idx) {
                return v;
            }
        }
        return null;
    }

    public WebVertex nearestVertexTo(Locatable to) {
        WebVertex best = null;
        double dist = 69_420;
        for (WebVertex vertex : this) {
            double distance = vertex.getTile().distance(to);
            if (distance < dist) {
                dist = distance;
                best = vertex;
            }
        }
        return best;
    }

    public WebPath pathToBank(WebBank bank) {
        return WebPath.build(bank.location());
    }

    public WebPath pathToNearestBank() {
        return pathToBank(nearestBank());
    }

    public WebBank nearestBank() {
        return WebBank.nearest();
    }

    public WebBank nearestBank(WebBank.Type type) {
        return WebBank.nearest(t -> t.type() == type);
    }

    public WebBank nearestBank(Filter<WebBank> filter) {
        return WebBank.nearest(filter);
    }

    public WebVertex nearestVertex() {
        return nearestVertexTo(Players.local());
    }

    public DijkstraPathfinder pathfinder() {
        return PATHFINDER;
    }
}

