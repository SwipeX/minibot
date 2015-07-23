package com.minibot.macros.zulrah.listener;

import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.LoopTask;

public abstract class ZulrahListener extends LoopTask {

    private Npc npc;
    private Tile previousTile = null;
    private int previousId = -1;
    private boolean wait = false;

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public Npc npc() {
        return npc;
    }

    public boolean validate() {
        return npc != null;
    }

    @Override
    public int loop() {
        if (npc != null) {
            int id = npc.id();
            Tile tile = npc.location();
            if (previousId != -1) {
                if (previousId != id) {
                    onChange(new ZulrahEvent(npc, previousTile, tile, previousId, id));
                } else if (previousTile.distance(tile) > 0) {
                    final int cachedId = id;
                    Time.sleep(() -> npc.id() != cachedId, 3000);
                    id = npc.id();
                    onChange(new ZulrahEvent(npc, previousTile, tile, previousId, id));
                }
            }
            previousId = id;
            previousTile = tile;
        } else {
            previousId = -1;
            previousTile = null;
        }
//        if (npc != null) {
//            Tile tile = npc.location();
//            int id = npc.id();
//            int height = npc.raw().getHeight();
//            if (height == 0) {
//                if (!wait) {
//                    previousId = id;
//                    wait = true;
//                }
//            } else {
//                boolean heightChange = (wait && height > 200);
//                boolean tileChange = (previousTile != null && previousTile.distance(tile) > 0);
//                boolean idChange = (previousId != -1 && previousId != id);
//                if (heightChange || tileChange || idChange) {
//                    if (previousTile != null && !(previousTile.distance(tile) == 0 && previousId == id)) {
//                        onChange(new ZulrahEvent(npc, previousTile, tile, previousId, id));
//                    }
//                    wait = false;
//                }
//            }
//            previousTile = tile;
//            previousId = id;
//        } else {
//            previousTile = null;
//            previousId = -1;
//            wait = false;
//        }
        return Random.nextInt(25, 50);
    }

    public abstract void onChange(ZulrahEvent event);
}