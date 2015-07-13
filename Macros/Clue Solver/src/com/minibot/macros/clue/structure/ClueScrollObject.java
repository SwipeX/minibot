package com.minibot.macros.clue.structure;

import com.minibot.api.method.Objects;
import com.minibot.api.method.Walking;
import com.minibot.api.method.web.WebPath;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Path;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.clue.TeleportLocation;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class ClueScrollObject extends ClueScroll {

    public final TeleportLocation teleport;
    public final String objectName;
    public final Tile walkingDestination, location;

    private boolean teleported;
    private boolean walked;

    public ClueScrollObject(int id, TeleportLocation teleport, String objectName, Tile walkingDestination,
                            Tile location) {
        super(id);
        this.teleport = teleport;
        this.objectName = objectName;
        this.walkingDestination = walkingDestination;
        this.location = location;
    }

    public ClueScrollObject(int id, TeleportLocation teleport, String objectName, Tile location) {
        this(id, teleport, objectName, location, location);
    }

    @Override
    public void reset() {
        teleported = false;
        walked = false;
    }

    @Override
    public void solve(AtomicReference<String> status) {
        if (teleport != null && !teleported) {
            status.set("Teleporting");
            teleported = teleport.teleport();
        } else {
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
                    GameObject object = Objects.findByName(location, objectName);
                    if (object != null) {
                        object.processFirstAction();
                        Time.sleep(800, 1200);
                    }
                }
            }
        }
    }
}