/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.wrapper;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public enum Prayer {

    THICK_SKIN(1, 0x1),
    BURST_OF_STRENGTH(4, 0x2),
    CLARITY_OF_THOUGHT(7, 0x4),
    ROCK_SKIN(10, 0x8),
    SHARP_EYE(8, 0x40000),
    MYSTIC_WILL(9, 0x80000),
    SUPERHUMAN_STRENGTH(13, 0x10),
    IMPROVED_REFLEXES(16, 0x20),
    RAPID_RESTORE(19, 0x40),
    RAPID_HEAL(22, 0x80),
    PROTECT_ITEM(25, 0x100),
    HAWK_EYE(26, 0x100000),
    MYSTIC_LORE(27, 0x200000),
    STEEL_SKIN(28, 0x200),
    ULTIMATE_STRENGTH(31, 0x400),
    INCREDIBLE_REFLEXES(34, 0x800),
    PROTECT_FROM_MAGIC(37, 0x1000),
    PROTECT_FROM_MISSILES(40, 0x2000),
    PROTECT_FROM_MELEE(43, 0x4000),
    EAGLE_EYE(44, 0x400000),
    MYSTIC_MIGHT(45, 0x800000),
    RETRIBUTION(46, 0x8000),
    REDEMPTION(49, 0x10000),
    SMITE(52, 0x20000),
    CHIVALRY(60, 0x2000000),
    PIETY(70, 0x4000000);

    private final int level;
    private final int bits;

    private Prayer(int level, int bits) {
        this.level = level;
        this.bits = bits;
    }

    public int level() {
        return this.level;
    }

    public int bits() {
        return this.bits;
    }

    public int componentIndex() {
        return super.ordinal();
    }

    public int widgetIndex() {
        return 271;
    }

    @Override
    public String toString() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }
}
