package com.minibot.api;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public interface Packet {

    public final int WALK_ON_SCREEN = 23;

    /**
     *  ITEM_PRIMARY works for: Spade's 'Dig', Trout's 'Eat', Teletab's 'break'
     */
    public int ITEM_USE = 38, ITEM_USE_ON_ITEM = 31, ITEM_DROP = 37, ITEM_EXAMINE = 1005, ITEM_CANCEL = 1006,
            ITEM_PRIMARY = 33, ITEM_WIELD = 34;

    public int ITEM_ON_OBJECT  = 1; // Using a 'Selected Item' on a SceneObject
    public int SPELL_ON_OBJECT = 2; // Using a 'Selected Spell' on a SceneObject

    public int OBJECT_ACTION_0 = 3;    // ^ Index 0
    public int OBJECT_ACTION_1 = 4;    // ^ Index 1
    public int OBJECT_ACTION_2 = 5;    // ^ Index 2
    public int OBJECT_ACTION_3 = 6;    // ^ Index 3
    public int OBJECT_ACTION_4 = 1001; // ^ Index 4

    public int ITEM_ON_NPC  = 7;  // Using a 'Selected Item' on a NPC
    public int SPELL_ON_NPC = 8;  // Using a 'Selected Spell' on a NPC

    public int NPC_ACTION_0 = 9;   // ^ Index 0
    public int NPC_ACTION_1 = 10;  // ^ Index 1
    public int NPC_ACTION_2 = 11;  // ^ Index 2
    public int NPC_ACTION_3 = 12;  // ^ Index 3
    public int NPC_ACTION_4 = 13;  // ^ Index 4
    public int EXAMINE_NPC  = 1003;

    public int ITEM_ON_PLAYER  = 14;  // Using a 'Selected Item' on a Player
    public int SPELL_ON_PLAYER = 15;  // Using a 'Selected Spell' on a Player

    public int PLAYER_ACTION_0 = 44; // ^ Index 0
    public int PLAYER_ACTION_1 = 45; // ^ Index 1
    public int PLAYER_ACTION_2 = 46; // ^ Index 2
    public int PLAYER_ACTION_3 = 47; // ^ Index 3
    public int PLAYER_ACTION_4 = 48; // ^ Index 4
    public int PLAYER_ACTION_5 = 49; // ^ Index 5
    public int PLAYER_ACTION_6 = 50; // ^ Index 6
    public int PLAYER_ACTION_7 = 51; // ^ Index 7

    public int ITEM_ON_GROUND_ITEM  = 16;
    public int SPELL_ON_GROUND_ITEM = 17;

    public int GROUND_ITEM_ACTION_0 = 18;
    public int GROUND_ITEM_ACTION_1 = 19;
    public int GROUND_ITEM_ACTION_2 = 20;
    public int GROUND_ITEM_ACTION_3 = 21;
    public int GROUND_ITEM_ACTION_4 = 22;
    public int EXAMINE_GROUND_ITEM  = 1004;

    public int BUTTON_INPUT   = 24; // Type 1
    public int BUTTON_SPELL   = 25; // Type 2
    public int BUTTON_CLOSE   = 26; // Type 3

    public int BUTTON_VARFLIP = 28; // Type 4
    public int BUTTON_VARSET  = 29; // Type 5
    public int BUTTON_DIALOG  = 30; // Type 6

    public int SPELL_ON_ITEM = 32; // 'Selected Spell' -> Item
    public int INTERFACE = 57;
}
