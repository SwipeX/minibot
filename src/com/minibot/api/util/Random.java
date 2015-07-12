package com.minibot.api.util;

import com.minibot.api.method.projection.Projection;

import java.awt.Point;
import java.awt.Shape;
import java.util.Collection;

public class Random {

    private static final java.util.Random GEN = new java.util.Random();

    public static int nextInt(int max) {
        return GEN.nextInt(max);
    }

    public static int nextInt(int min, int max) {
        return min + nextInt(max - min);
    }

    public static double nextDouble() {
        return GEN.nextDouble();
    }

    public static boolean nextBoolean() {
        return GEN.nextBoolean();
    }

    @SafeVarargs
    public static <T> T nextElement(T... elements) {
        return elements[nextInt(elements.length - 1)];
    }

    @SuppressWarnings("unchecked")
    public static <T> T nextElement(Collection<T> elements) {
        Object[] array = elements.toArray();
        return (T) nextElement(array);
    }

    public static Point nextPoint(Shape shape) {
        return nextElement(Projection.getPointsIn(shape));
    }

}