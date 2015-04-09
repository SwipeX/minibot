package com.minibot.api.util;

/**
 * @author Tyler Sedlar
 * @since 3/2/2015
 */
public class Array {

    @SuppressWarnings("unchecked")
    public static <E> E[] sizeUp(E[] array) {
        Class<?> arrayType = array.getClass().getComponentType();
        E[] newArray = (E[]) java.lang.reflect.Array.newInstance(arrayType, array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] trim(E[] array, int size) {
        try {
            Class<?> arrayType = array.getClass().getComponentType();
            E[] newArray = (E[]) java.lang.reflect.Array.newInstance(arrayType, size);
            System.arraycopy(array, 0, newArray, 0, size);
            return newArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            return array;
        }
    }

    public static <E> E[] add(E[] array, E element) {
        array = sizeUp(array);
        array[array.length - 1] = element;
        return array;
    }
}
