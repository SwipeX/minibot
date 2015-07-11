package com.minibot.api.wrapper;

import com.minibot.api.method.Game;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public interface Path extends Iterable<Tile> {

    Tile[] toArray();

    boolean step(Option... options);

    public static enum Option {

        TOGGLE_RUN {
            @Override
            public void handle() {
                if (!Game.runEnabled()) {
                    Game.setRun(true);
                }
            }
        };

        public abstract void handle();
    }
}
