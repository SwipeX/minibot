package com.minibot.api.wrapper.locatable;

/**
 * @author Tyler Sedlar
 */
public interface Locatable {

    public Tile location();

    public int distance(Locatable locatable);

    public int distance();
}
