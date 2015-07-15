package com.minibot.macros.zulrah;

import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public class Capture {
    private int previousAnimation = -1;
    private int previousId = -1;
    private int previousHealth = -1;
    private Tile previousLocation = null;

    public void capture(Npc npc) {
        if (npc != null && npc.name().equals("Zulrah")) {
            this.previousAnimation = npc.animation();
            this.previousId = npc.id();
            this.previousHealth = npc.health();
            this.previousLocation = npc.location();
        } else {//not 100% sure we want to do this
            previousId = -1;
            previousLocation = null;
            previousAnimation = -1;
            previousHealth = -1;
        }
    }

    public int getPreviousAnimation() {
        return previousAnimation;
    }

    public int getPreviousId() {
        return previousId;
    }

    public int getPreviousHealth() {
        return previousHealth;
    }

    public Tile getPreviousLocation() {
        return previousLocation;
    }
}
