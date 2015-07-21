package com.minibot.macros.zulrah.util;

import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.LoopTask;

public abstract class ZulrahListener extends LoopTask {

        private Npc npc;
        private Tile previousTile = null;
        private int previousId = -1;

        private long lastChange = -1;

        public void setNpc(Npc npc) {
            this.npc = npc;
        }

        public boolean validate() {
            return npc != null;
        }

        @Override
        public int loop() {
            if (npc != null) {
                Tile tile = npc.location();
                int id = npc.id();
                if ((previousId != -1 && id != previousId) || (previousTile != null && (tile.x() != previousTile.x() ||
                        tile.y() != previousTile.y()))) {
                    if (lastChange == -1 || Time.millis() - lastChange  > 500) {
                        lastChange = Time.millis();
                        onChange(new ZulrahEvent(npc, previousTile, tile, previousId, id));
                    }
                }
                previousTile = tile;
                previousId = id;
            } else {
                previousTile = null;
                previousId = -1;
            }
            return Random.nextInt(25, 50);
        }

        public abstract void onChange(ZulrahEvent event);
    }