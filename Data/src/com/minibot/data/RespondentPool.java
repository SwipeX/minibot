package com.minibot.data;

import com.minibot.data.respondents.Chin;
import com.minibot.data.respondents.Finish;
import com.minibot.data.respondents.Respondent;
import com.minibot.data.respondents.Start;

import java.util.HashMap;

/**
 * Created by tim on 6/3/15.
 */
public class RespondentPool {

    private static HashMap<String, Respondent> tasks = new HashMap<>();

    static {
        tasks.put("START", new Start());
        tasks.put("FINISH", new Finish());
        tasks.put("CHIN", new Chin());
    }

    public static void process(String raw) {
        String[] commands = raw.split("&");
        if (tasks.containsKey(commands[0])) {
            Respondent respondent = tasks.get(commands[0]);
            if (respondent != null) {
                new Thread(respondent.getRunnable(commands)).start();
            }
        }
    }
}
