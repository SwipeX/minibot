package com.minibot.api.util;

import com.minibot.api.method.projection.Rendering;

import java.awt.*;
import java.util.Collection;

/**
 * @author Tyler Sedlar
 */
public class Random {

    private static final java.util.Random RANDOM = new java.util.Random();

    public static int nextInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static int nextInt(int min, int max) {
        return min + nextInt(max - min);
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static <T> T nextElement(T[] elements) {
        return elements[nextInt(elements.length - 1)];
    }

    @SuppressWarnings("unchecked")
    public static <T> T nextElement(Collection<T> elements) {
        Object[] array = elements.toArray();
        return (T) nextElement(array);
    }

    public static Point nextPoint(Shape shape) {
        return nextElement(Rendering.points(shape));
    }

}
