package com.minibot.macros.zulrah;

public enum ZulrahDirection {

    LEFT,
    BOTTOM,
    RIGHT;

    @Override
    public String toString() {
        String superString = super.toString();
        char first = Character.toUpperCase(superString.charAt(0));
        return first + superString.substring(1).toLowerCase();
    }
}