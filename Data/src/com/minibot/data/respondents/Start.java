package com.minibot.data.respondents;

import com.minibot.data.Database;

/**
 * Created by tim on 6/3/15.
 * usage: 'START RSN MACRO'
 */
public class Start implements Respondent {
    @Override
    public Runnable getRunnable(String[] commands) {
        if (commands.length != 3) return null;
        String rsn = commands[1];
        String script = commands[2];
        return () -> Database.activity(0, rsn, script);
    }
}
