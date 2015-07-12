package com.minibot.macros.clue.structure.location;

import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.clue.TeleportLocation;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class MediumClueSource extends ClueSource {

    private static final Area FALADOR = new Area(new Tile(2938, 3357, 0), new Tile(2979, 3404, 0));
    private static final Tile FALADOR_BANK = new Tile(2946, 3369, 0);
    private static final Tile FALADOR_GUARDS = new Tile(2966, 3393, 0);

    public MediumClueSource() {
        super(FALADOR, FALADOR_BANK, FALADOR_GUARDS, TeleportLocation.FALADOR, n -> {
            if (n.dead())
                return false;
            String name = n.name();
            return name != null && name.equals("Guard") && n.level() < 22 && n.distance(FALADOR_GUARDS) < 8;
        });
    }
}
