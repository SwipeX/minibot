package com.minibot.data.respondents;

/**
 * @author Tim Dekker
 * @since 6/3/15
 */
public interface Respondent {

    Runnable getRunnable(String... commands);
}