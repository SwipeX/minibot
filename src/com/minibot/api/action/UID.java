package com.minibot.api.action;

public class UID { // 31 bit value

    // The only -intractable- entity types (Note no projectiles/graphics)
    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_NPC = 1;
    public static final int TYPE_OBJECT = 2;
    public static final int TYPE_GROUND_ITEM = 3;

    public final int uid;

    public UID(int uid) {
        this.uid = uid;
    }

    public int localX() {
        return regionX(uid);
    }

    public int localY() {
        return regionY(uid);
    }

    public int entityId() {
        return entityId(uid);
    }

    public int entityType() {
        return entityType(uid);
    }

    public boolean interactable() {
        return interactable(uid);
    }

    public static int compile(int regionX, int regionY, int entityId, int entityType, boolean intractable) {
        regionX &= 127;    // Maximum value of 127
        regionY &= 127;    // Maximum value of 127
        entityId &= 32767;  // Maximum value of 32767
        entityType &= 3;      // Maximum value of 3
        int uid = entityType << 29 + entityId << 14 + regionY << 7 + regionX;
        if (!intractable)
            uid -= Integer.MIN_VALUE; //Set the sign bit to 1
        return uid;
    }

    public static int regionX(int UID) {
        return UID & 0x7f;
    }

    public static int regionY(int UID) {
        return UID >> 7 & 0x7f;
    }

    public static int entityId(int UID) {
        return UID >> 14 & 0x7fff;
    }

    public static int entityType(int UID) {
        return UID >> 29 & 0x3;
    }

    //Checks the sign bit, checking if it's positive or negative is a faster/clever alternative
    public static boolean interactable(int UID) {
        return UID > 0;
    }
}
