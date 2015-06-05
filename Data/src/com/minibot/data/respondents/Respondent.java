package com.minibot.data.respondents;

/**
 * Created by tim on 6/3/15.
 */
public interface Respondent {
    Runnable getRunnable(String[] commands);
}
