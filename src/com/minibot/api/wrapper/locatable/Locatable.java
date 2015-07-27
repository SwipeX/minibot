package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.Players;
import com.minibot.api.method.projection.Projection;

/**
 * @author Tyler Sedlar
 */
public interface Locatable {

    Tile location();

    int distance(Locatable locatable);

    int distance();

    default double exactDistance(Locatable locatable) {
        return Projection.distance(this, locatable);
    }

    default double exactDistance() {
        return exactDistance(Players.local());
    }
}