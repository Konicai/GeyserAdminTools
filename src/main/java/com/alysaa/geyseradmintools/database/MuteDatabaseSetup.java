package com.alysaa.geyseradmintools.database;

import com.alysaa.geyseradmintools.Gat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.*;

public class MuteDatabaseSetup {
    private static Connection connection;
    public String host;
    public String database;
    public String username;
    public String password;
    public static String Mutetable;
    public int port;

    public void mysqlSetup() {
        host = Gat.plugin.getConfig().getString("host");
        port = Gat.plugin.getConfig().getInt("port");
        database = Gat.plugin.getConfig().getString("database");
        username = Gat.plugin.getConfig().getString("username");
        password = Gat.plugin.getConfig().getString("password");
        Mutetable = "Mute_list";
        if (Gat.plugin.getConfig().getBoolean("EnableMySQL")) {
            try {
                synchronized (this) {
                    if (getConnection() != null && !getConnection().isClosed()) {
                        return;
                    }

                    Class.forName("com.mysql.jdbc.Driver");
                    setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                            + this.port + "/" + this.database, this.username, this.password));
                    createTable();

                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[GeyserAdminTools] MYSQL Mute Connected");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Class.forName("org.sqlite.JDBC");
                setConnection(DriverManager.getConnection("jdbc:sqlite:plugins/GeyserAdminTools/database.db"));
                String cmd = "CREATE TABLE IF NOT EXISTS " + MuteDatabaseSetup.Mutetable + " (UUID char(36), Reason varchar(500), Username varchar(16), EndDate varchar(500))";
                PreparedStatement stmt = connection.prepareStatement(cmd);
                stmt.execute();
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[GeyserAdminTools] SQLite Mute Connected");
            } catch (Exception e) {
                System.out.println("SQLite Error");
                e.printStackTrace();
            }
        }
    }
    public static void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + MuteDatabaseSetup.Mutetable + " (UUID char(36), Reason varchar(500), Username varchar(16), EndDate varchar(500))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        MuteDatabaseSetup.connection = connection;
    }
}