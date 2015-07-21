package com.minibot.macros.zulrah.util;

import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;

public class ZulrahEvent {

        public final Npc npc;
        public final Tile previousTile, tile;
        public final int previousId, id;

        public ZulrahEvent(Npc npc, Tile previousTile, Tile tile, int previousId, int id) {
            this.npc = npc;
            this.previousTile = previousTile;
            this.tile = tile;
            this.previousId = previousId;
            this.id = id;
        }
    }