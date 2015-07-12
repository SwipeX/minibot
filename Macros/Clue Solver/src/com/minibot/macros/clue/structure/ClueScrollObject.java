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
    public final String objectName, actionName;
    public final Tile walkingDestination, location;

    private boolean teleported = false;
    private boolean walked = false;

    public ClueScrollObject(int id, TeleportLocation teleport, String objectName, String actionName,
                            Tile walkDesintination, Tile location) {
        super(id);
        this.teleport = teleport;
        this.objectName = objectName;
        this.actionName = actionName;
        this.walkingDestination = walkDesintination;
        this.location = location;
    }

    public ClueScrollObject(int id, TeleportLocation teleport, String objectName, String actionName, Tile location) {
        this(id, teleport, objectName, actionName, location, location);
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
                    GameObject[] objects = Objects.allAt(location);
                    for (GameObject object : objects) {
                        String name = object.name();
                        if (name != null && name.equals(objectName)) {
                            object.processAction(actionName);
                            Time.sleep(800, 1200);
                            break;
                        }
                    }
                }
            }
        }
    }
}
