package com.minibot.bot.breaks;

import com.minibot.api.util.Random;

/**
 * Created by tim on 6/1/15.
 */
public class IntervalHandler extends BreakHandler {
    private static final int DEFAULT_SIZE = 10;
    private static final int SIX_HOURS = 6 * 60 * 60 * 1000; //H * M * S * MS -> THE    JVM WILL CONSTANT FOLD THIS
    private static final int THIRTY_MINUTES = 30 * 60 * 1000;

    @Override
    public void init() {
        //setup accounts order and time
        times = new long[DEFAULT_SIZE];
        lengths = new int[DEFAULT_SIZE];
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            if (i == 0) {
                times[0] = System.currentTimeMillis() + SIX_HOURS +
                        Random.nextInt(-1 * THIRTY_MINUTES, THIRTY_MINUTES);                ;
            } else {
                times[i] = times[i - 1] + (2 * SIX_HOURS) + //last break + 12 hrs
                        Random.nextInt(-1 * THIRTY_MINUTES, THIRTY_MINUTES);
            }
            ; //6 hours from point times[i] +- 30M
            lengths[i] = SIX_HOURS + Random.nextInt(-1 * THIRTY_MINUTES, THIRTY_MINUTES);//all breaks will be the same six hours +- 30M
        }
    }
}
