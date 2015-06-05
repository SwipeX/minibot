package com.minibot.data.respondents;

import com.minibot.data.Database;

/**
 * Created by tim on 6/3/15.
 */
public class Chin implements Respondent {
    @Override
    public Runnable getRunnable(String[] commands) {
        if (commands.length < 4) return null;
        String rsn = commands[1];
        int chins = Integer.parseInt(commands[2]);
        int runtime = Integer.parseInt(commands[3]);
        return () -> Database.chin(rsn, chins, runtime);
    }
}
