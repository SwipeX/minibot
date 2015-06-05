package com.minibot.data;

import java.sql.*;

/**
 * Created by tim on 6/3/15.
 */
public class Database {
    private static Connection connection;

    static {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/minibot");
            Statement stat = connection.createStatement();
            stat.execute("CREATE TABLE IF NOT EXISTS activity(name VARCHAR(255) PRIMARY KEY, macro VARCHAR(255), type INT, stamp TIMESTAMP)");
            stat.execute("CREATE TABLE IF NOT EXISTS chin(name VARCHAR(255) PRIMARY KEY, chins INT, runtime INT)");
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    //type = 0 = start,
    //type = 1 = stop
    public static void activity(int type, String name, String macro) {
        try {
            PreparedStatement stat = connection.prepareStatement(String.format("insert into activity values('%s','%s', %s, ?)", name, macro, type,
                    ""));
            stat.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stat.execute();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void chin(String name, int chins, int runtime) {
        try {
            Statement stat = connection.createStatement();
            stat.execute(String.format("insert into chin values('%s', %s, %s)", name, chins, runtime));
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
