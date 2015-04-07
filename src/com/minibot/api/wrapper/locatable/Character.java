package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.mod.hooks.ReflectionData;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@ReflectionData(className = "Character")
public class Character extends Wrapper implements Locatable {

    public Character(Object raw) {
        super(raw);
    }

    public int fineX() {
        return hook("Character", "x").getInt(get());
    }

    public int fineY() {
        return hook("Character", "y").getInt(get());
    }

    public int localX() {
        return fineX() >> 7;
    }

    public int localY() {
        return fineY() >> 7;
    }

    public int x() {
        return Game.baseX() + localX();
    }

    public int y() {
        return Game.baseY() + localY();
    }

    public int health() {
        return hook("Character", "health").getInt(get());
    }

    public int maxHealth() {
        return hook("Character", "health").getInt(get());
    }

    public int interactingIndex() {
        return hook("Character", "interactingIndex").getInt(get());
    }

    public int animation() {
        return hook("Character", "animation").getInt(get());
    }

    @Override
    public Tile location() {
        return new Tile(x(), y(), Game.plane());
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return distance(Players.local());
    }
}
