package com.minibot.data;

import java.sql.*;

/**
 * @author Tim Dekker
 * @since 6/3/15
 */
public class Database {

    private static Connection connection;

    static {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/minibot");
            Statement stat = connection.createStatement();
            stat.execute("CREATE TABLE IF NOT EXISTS activity(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(255), macro VARCHAR(255), type INT, stamp TIMESTAMP)");
            stat.execute("CREATE TABLE IF NOT EXISTS chin(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(255), chins INT, runtime INT, stamp TIMESTAMP)");
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

    public static ResultSet query(String rawSQL) {
        try {
            return connection.createStatement().executeQuery(rawSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //type = 0 = start,
    //type = 1 = stop
    public static void activity(int type, String name, String macro) {
        try {
            PreparedStatement stat = connection.prepareStatement(String.format("insert into activity(name, macro, " +
                    "type, stamp) values('%s','%s', %s, ?)", name, macro, type));
            stat.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stat.execute();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void chin(String name, int chins, int runtime) {
        try {
            PreparedStatement stat = connection.prepareStatement(String.format("insert into chin(name, chins, " +
                    "runtime, stamp) values('%s', %s, %s, ?)", name, chins, runtime));
            stat.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stat.execute();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}