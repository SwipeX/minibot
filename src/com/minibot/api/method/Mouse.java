package com.minibot.api.method;

import com.minibot.Minibot;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/6/15.
 */
public class Mouse {

    public static int x() {
        return Minibot.instance().canvas().mouseX;
    }

    public static int y() {
        return Minibot.instance().canvas().mouseY;
    }

    public static Point location() {
        return new Point(x(), y());
    }

    public static void hop(int x, int y) {
        Minibot.instance().canvas().moveMouse(x, y);
    }
}
