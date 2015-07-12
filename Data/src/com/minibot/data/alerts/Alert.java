package com.minibot.data.alerts;

/**
 * @author Tim Dekker
 * @since 6/5/15
 */
public interface Alert extends Runnable {

    boolean validate();
}