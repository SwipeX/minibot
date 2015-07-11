package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.Game;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.Wrapper;
import com.minibot.client.natives.RSCharacter;
import com.minibot.client.natives.RSNpc;
import com.minibot.client.natives.RSPlayer;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public abstract class Character<T extends RSCharacter> extends Wrapper<T> implements Locatable {

    public Character(T raw) {
        super(raw);
    }

    public int fineX() {
        return raw.getX();
    }

    public int fineY() {
        return raw.getY();
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
        return raw.getHealth();
    }

    public int maxHealth() {
        return raw.getMaxHealth();
    }

    public int healthPercent() {
        int health = health();
        int maxHealth = maxHealth();
        return maxHealth == 0 ? -1 : (int) (((double) health / (double) maxHealth) * 100D);
    }

    public int targetIndex() {
        return raw.getInteractingIndex();
    }

    public int animation() {
        return raw.getAnimation();
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

    public Point screen(){
        return Projection.groundToViewport(fineX(), fineY());
    }

    public  boolean interacting() {
        Player player = Players.local();
        if (player != null) {
            Character target = player.target();
            return target != null && target.targetIsLocalPlayer();
        }
        return false;
    }

    public boolean dead() {
        return maxHealth() > 0 && health() == 0;
    }

    public final Character target() {
        int index = targetIndex();
        if (index == -1 || index == 65535)
            return null;
        if (index < 0x8000) {
            RSNpc[] npcs = Npcs.raw();
            if (npcs == null || npcs.length == 0)
                return null;
            if (npcs[index] != null)
                return new Npc(npcs[index], index);
        } else if (index - 0x8000 == Game.localPlayerIndex()) {
            return Players.local();
        } else {
            RSPlayer[] players = Players.raw();
            if (players == null || players.length == 0)
                return null;
            if (players[index - 0x8000] != null)
                return new Player(players[index - 0x8000], index - 0x8000);
        }
        return null;
    }

    public boolean targetIsLocalPlayer() {
        int targetIndex = targetIndex();
        return targetIndex != -1 && (targetIndex - 0x8000) == Game.localPlayerIndex();
    }
}