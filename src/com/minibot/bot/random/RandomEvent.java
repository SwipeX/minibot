package com.minibot.bot.random;

import com.minibot.api.util.Renderable;
import com.minibot.client.GameCanvas;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
public abstract class RandomEvent implements Renderable {

    public static final RandomEvent[] SOLVERS = {new PinSolver(), new Dismisser()};

    static {
        GameCanvas.addRenderable((g) -> {
            for (RandomEvent random : SOLVERS) {
                if (random.solving)
                    random.render(g);
            }
        });
    }

    public boolean solving = false;

    public abstract boolean validate();
    public abstract void run();
}
