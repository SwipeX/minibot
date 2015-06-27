package com.minibot.api.wrapper.locatable;

/**
 * @author Tyler Sedlar
 */
public interface Locatable {

    Tile location();

    int distance(Locatable locatable);
    int distance();
}