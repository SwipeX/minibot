package com.minibot.api.util.filter;

public interface Filter<E> {

    boolean accept(E e);

    /**
     * @param <T> the type
     * @return a Filter that always accepts the element
     */
    static <T> Filter<T> always() {
        return t -> true;
    }

    /**
     * @param <T> the type
     * @return a Filter that always rejects the element
     */
    static <T> Filter<T> never() {
        return t -> false;
    }

    static <E> Filter<E> not(Filter<E> filter) {
        return e -> !filter.accept(e);
    }
}
