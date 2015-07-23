package com.minibot.macros.zulrah.listener;

import com.minibot.api.util.Random;
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
            Tile tile = npc.location();
            int id = npc.id();
            int height = npc.raw().getHeight();
            if (height < 200) {
                if (!wait) {
                    previousTile = tile;
                    previousId = id;
                    wait = true;
                }
            } else {
                if (wait && (height > 250 || (previousTile != null && previousTile.distance(tile) > 0))) {
                    if (!(previousTile.distance(tile) == 0 && previousId == id)) {
                        onChange(new ZulrahEvent(npc, previousTile, tile, previousId, id));
                    }
                    previousTile = tile;
                    previousId = id;
                    wait = false;
                }
            }
        } else {
            previousTile = null;
            previousId = -1;
        }
        return Random.nextInt(25, 50);
    }

    public abstract void onChange(ZulrahEvent event);
}