package com.minibot.macros;

import com.minibot.api.method.Npcs;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.LoopTask;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    private class ZulrahEvent {

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

    private abstract class ZulrahListener extends LoopTask {

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

    private final ZulrahListener listener = new ZulrahListener() {
        public void onChange(ZulrahEvent event) {
            System.out.println("id: " + event.previousId + " -> " + event.id);
            System.out.println("tile: " + event.previousTile + " -> " + event.tile);
            System.out.println("--------------------------");
        }
    };

    @Override
    public void atStart() {
        listener.start();
    }

    @Override
    public void run() {
        Npc zulrah = Npcs.nearestByName("Zulrah");
//        if (!listener.validate()) {
            listener.setNpc(zulrah);
//        }
    }


    @Override
    public void render(Graphics2D g) {
    }
}