package com.minibot.bot.breaks;

/**
 * Created by tim on 6/1/15.
 */
public class RotationHandler extends BreakHandler {

    private static final int SIX_HOURS = 6 * 60 * 60 * 1000; //H * M * S * MS -> THE    JVM WILL CONSTANT FOLD THIS

    @Override
    public void init() {
        //set up accounts, once six hours is up switch accounts and macros.
        //could possibly just wait until auto-log, though using time may be better.
    }
}
