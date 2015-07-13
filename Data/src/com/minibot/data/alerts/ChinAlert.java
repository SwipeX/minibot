package com.minibot.data.alerts;

import com.minibot.data.Database;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 6/5/15
 */
public class ChinAlert extends TimedAlert {

    private final String account;

    public ChinAlert(long interval, String account) {
        super(interval);
        this.account = account;
    }

    @Override
    public void run() {
        try {
            List<ChinEntry> entries = new ArrayList<>();
            ResultSet results = Database.query("SELECT name,chins,runtime,stamp FROM chin WHERE name=" + account);
            if (results != null) {
                while (results.next()) {
                    Timestamp time = results.getTimestamp(4);
                    if (time != null) {
                        long millis = time.getTime();
                        if (System.currentTimeMillis() - millis < interval) {
                            String name = results.getString(1);
                            int chins = results.getInt(2);
                            int runtime = results.getInt(3);
                            entries.add(new ChinEntry(name, chins, runtime));
                        }
                    }
                }
                //send email/text/ect
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChinEntry {

        private final String name;
        private final int chins;
        private final int runtime;

        public ChinEntry(String name, int runtime, int chins) {
            this.name = name;
            this.runtime = runtime;
            this.chins = chins;
        }
    }
}