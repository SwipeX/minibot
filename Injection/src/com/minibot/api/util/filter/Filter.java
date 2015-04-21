package com.minibot.api.util.filter;

public interface Filter<E> {

    public boolean accept(E e);

    /**
     * @param <T> the type
     * @return a Filter that always accepts the element
     */
    public static <T> Filter<T> always() {
        return t -> true;
    }

    /**
     * @param <T> the type
     * @return a Filter that always rejects the element
     */
    public static <T> Filter<T> never() {
        return t -> false;
    }

    public static <E> Filter<E> not(Filter<E> filter) {
        return e -> !filter.accept(e);
    }
}
