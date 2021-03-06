package com.projectg.geyseradmintools.forms;

import com.projectg.geyseradmintools.database.DatabaseSetup;
import com.projectg.geyseradmintools.language.Messages;
import com.projectg.geyseradmintools.utils.CheckJavaOrFloodPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BanPlayerForm {
    public static void banList(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
        if (isFloodgatePlayer) {
            FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(uuid);
            fPlayer.sendForm(
                    SimpleForm.builder()
                            .title(ChatColor.DARK_AQUA + Messages.get("main.ban.form.title"))
                            .button(ChatColor.DARK_AQUA + Messages.get("main.ban.form.button1"))
                            .button(ChatColor.DARK_AQUA + Messages.get("main.ban.form.button2"))
                            .responseHandler((form, responseData) -> {
                                SimpleFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    // player closed the form or returned invalid info (see FormResponse)
                                    return;
                                }
                                if (response.getClickedButtonId() == 0) {
                                    if (player.hasPermission("geyseradmintools.banplayer")) {
                                        banPlayers(player);
                                    } else {
                                        player.sendMessage(ChatColor.RED + Messages.get("permission.button.error"));
                                    }
                                }
                                if (response.getClickedButtonId() == 1) {
                                    if (player.hasPermission("geyseradmintools.banplayer")) {
                                        unbanPlayers(player);
                                    } else {
                                        player.sendMessage(ChatColor.RED + Messages.get("permission.button.error"));
                                    }
                                }
                            }));
        }
    }

    public static void banPlayers(Player player) {
        Runnable runnable = () -> {
            UUID uuid = player.getUniqueId();
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            String[] playerList = names.toArray(new String[0]);
            boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
            if (isFloodgatePlayer) {
                FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(uuid);
                fPlayer.sendForm(
                        CustomForm.builder()
                                .title(ChatColor.DARK_AQUA + Messages.get("ban.ban.form.title"))
                                .dropdown(ChatColor.DARK_AQUA + Messages.get("ban.ban.form.dropdown"), playerList)
                                .input(ChatColor.DARK_AQUA + Messages.get("ban.ban.form.input1"))
                                .input(ChatColor.DARK_AQUA + Messages.get("ban.ban.form.input2"))
                                .responseHandler((form, responseData) -> {
                                    CustomFormResponse response = form.parseResponse(responseData);
                                    if (!response.isCorrect()) {
                                        return;
                                    }
                                    int clickedIndex = response.getDropdown(0);
                                    String day = response.getInput(1);
                                    String time;
                                    try {
                                        time = LocalDate.now().plusDays(Long.parseLong(day)).toString();
                                    } catch (NumberFormatException | NullPointerException  e) {
                                        player.sendMessage(ChatColor.YELLOW + Messages.get("ban.input.error"));
                                   return;
                                    }
                                    String reason = response.getInput(2);
                                    String name = names.get(clickedIndex);
                                    Player player1 = Bukkit.getPlayer(name);
                                    //database code
                                    try {
                                        String sql = "(UUID,REASON,USERNAME,ENDDATE) VALUES (?,?,?,?)";
                                        PreparedStatement insert = DatabaseSetup.getConnection().prepareStatement("INSERT INTO " + DatabaseSetup.banTable
                                                + sql);
                                        insert.setString(1, player1.getUniqueId().toString());
                                        insert.setString(2, reason);
                                        insert.setString(3, name);
                                        insert.setString(4, time);
                                        insert.executeUpdate();
                                        // Player inserted now
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    player1.kickPlayer(Messages.get("ban.ban.form.player.message1",reason,time));
                                    player.sendMessage(ChatColor.GOLD + Messages.get("ban.ban.form.player.message2",name));
                                    //end
                                }));
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void unbanPlayers(Player player) {
        Runnable runnable = () -> {
            UUID uuid = player.getUniqueId();
            List<String> names = new ArrayList<>();
            String query = "SELECT * FROM " + DatabaseSetup.banTable;
            try (Statement stmt = DatabaseSetup.getConnection().createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    names.add(rs.getString("Username"));
                }
                rs.close();
                String[] playerList = names.toArray(new String[0]);
                boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
                if (isFloodgatePlayer) {
                    FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(uuid);
                    fPlayer.sendForm(
                            CustomForm.builder()
                                    .title(ChatColor.DARK_AQUA + Messages.get("unban.ban.form.title"))
                                    .dropdown(ChatColor.DARK_AQUA + Messages.get("unban.ban.form.dropdown"), playerList)
                                    .responseHandler((form, responseData) -> {
                                        CustomFormResponse response = form.parseResponse(responseData);
                                        if (!response.isCorrect()) {
                                            return;
                                        }
                                        int clickedIndex = response.getDropdown(0);
                                        String name = names.get(clickedIndex);
                                        OfflinePlayer player1 = Bukkit.getOfflinePlayer(name);
                                        //MySQL code
                                        try {
                                            PreparedStatement statement = DatabaseSetup.getConnection()
                                                    .prepareStatement("DELETE FROM " + DatabaseSetup.banTable + " WHERE UUID=?");
                                            statement.setString(1, player1.getUniqueId().toString());
                                            statement.execute();
                                            player.sendMessage(ChatColor.GREEN +Messages.get("unban.ban.form.player.message1",name));
                                        } catch (SQLException exe) {
                                            exe.printStackTrace();
                                        }
                                    }));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}