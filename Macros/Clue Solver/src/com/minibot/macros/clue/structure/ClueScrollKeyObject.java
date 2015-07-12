package com.minibot.macros.clue.structure;

import com.minibot.api.method.*;
import com.minibot.api.method.web.WebPath;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Path;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.clue.TeleportLocation;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class ClueScrollKeyObject extends ClueScroll {

    public final TeleportLocation teleport;
    public final Tile keyWalkDestination, walkingDestination, location;
    public final String keyNpcName, objectName;

    private boolean teleported = false;
    private boolean keyWalked = false;
    private boolean walked = false;

    public ClueScrollKeyObject(int id, TeleportLocation teleport, Tile keyWalkDestination, String keyNpcName,
                               String objectName, Tile walkingDestination, Tile location) {
        super(id);
        this.teleport = teleport;
        this.keyWalkDestination = keyWalkDestination;
        this.keyNpcName = keyNpcName;
        this.objectName = objectName;
        this.walkingDestination = walkingDestination;
        this.location = location;
    }

    public ClueScrollKeyObject(int id, TeleportLocation teleport, Tile keyWalkDestination, String keyNpcName,
                               String objectName, Tile location) {
        this(id, teleport, keyWalkDestination, keyNpcName, objectName, location, location);
    }

    @Override
    public void reset() {
        teleported = false;
        keyWalked = false;
        walked = false;
    }

    @Override
    public void solve(AtomicReference<String> status) {
        if (teleport != null && !teleported) {
            status.set("Teleporting");
            teleported = teleport.teleport();
        } else {
            if (Inventory.first(i -> {
                String name = i.name();
                return name != null && name.contains("Key");
            }) != null) {
                if (walkingDestination.distance() > 2 && !walked) {
                    status.set("Walking to destination");
                    WebPath.build(walkingDestination).step(Path.Option.TOGGLE_RUN);
                } else {
                    walked = true;
                    if (location.distance() > 5) {
                        status.set("Walking to destination");
                        Walking.walkTo(location);
                        Time.sleep(600, 800);
                    } else {
                        status.set("Interacting");
                        GameObject[] objects = Objects.allAt(location);
                        for (GameObject object : objects) {
                            String name = object.name();
                            if (name != null && name.equals(objectName)) {
                                object.processFirstAction();
                                Time.sleep(800, 1200);
                                break;
                            }
                        }
                    }
                }
            } else {
                if (keyWalkDestination.distance() > 2 && !keyWalked) {
                    status.set("Walking to key");
                    WebPath.build(keyWalkDestination).step(Path.Option.TOGGLE_RUN);
                } else {
                    GroundItem key = Ground.nearestByFilter(i -> {
                        String name = i.name();
                        return name != null && name.contains("Key");
                    });
                    if (key != null) {
                        status.set("Taking key");
                        key.take();
                    } else {
                        status.set("Killing key-holder");
                        Npc npc = Npcs.nearestByName(keyNpcName);
                        if (npc != null)
                            npc.attack();
                    }
                }
            }
        }
    }
}
