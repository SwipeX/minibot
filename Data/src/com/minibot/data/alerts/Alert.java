package com.minibot.data.alerts;

/**
 * Created by tim on 6/5/15.
 */
public interface Alert extends Runnable{
    boolean validate();
}
