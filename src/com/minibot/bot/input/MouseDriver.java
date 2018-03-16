package com.minibot.bot.input;

import com.minibot.Minibot;
import com.minibot.api.util.Random;
import com.minibot.client.GameCanvas;
/**
 * Will see if moving mouse randomly will keep ban away
 * Will move and click along top inv tabs
 */
public class MouseDriver {

    public boolean alive = true;
    private static MouseDriver instance;

    public static MouseDriver getInstance() {
        return (instance == null ? instance = new MouseDriver() : instance);
    }

    public MouseDriver() {
        instance = this;
    }

    public void randomMouse() {
        new Thread(() -> {
            while (alive) {
                GameCanvas canvas = Minibot.instance().canvas();
                int randX = Random.nextInt(527, 752);
                int randY = Random.nextInt(172, 201);
                canvas.moveMouse(randX, randY);
                if (Random.nextInt(1, 30) == 21) {
                    canvas.clickMouse(Random.nextBoolean());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
