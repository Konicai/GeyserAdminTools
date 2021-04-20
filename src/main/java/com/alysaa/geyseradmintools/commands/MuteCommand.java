package com.alysaa.geyseradmintools.commands;

import com.alysaa.geyseradmintools.Gat;
import com.alysaa.geyseradmintools.database.BanDatabaseSetup;
import com.alysaa.geyseradmintools.database.MuteDatabaseSetup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class MuteCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "The console cannot use this command");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("gmute") && player.hasPermission("geyseradmintools.gmute")) {
            try {
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "[GeyserAdminTools] Could not find player! Perhaps wrong usage ? /gmute <username> <days> <reason>");
                    return true;
                }
                String day = args[1];
                String time = LocalDate.now().plusDays(Long.parseLong(day)).toString();
                String reason = args[2];
                String sql = "(UUID,REASON,USERNAME,ENDDATE) VALUES (?,?,?,?)";
                PreparedStatement insert = BanDatabaseSetup.getConnection().prepareStatement("INSERT INTO " + MuteDatabaseSetup.Mutetable
                        + sql);
                insert.setString(1, target.getUniqueId().toString());
                insert.setString(2, reason);
                insert.setString(3, target.getName());
                insert.setString(4, time);
                insert.executeUpdate();
                target.sendMessage("you where muted till: " + time + "for: " + reason);
                player.sendMessage("[GeyserAdminTools] Player " + target.getName() + " is muted");
                Gat.logger.info("Player " + player.getName() + " has muted " + target.getName() + " till: " + time + " for reason: " + reason);
            }catch (IllegalArgumentException | CommandException e) {
                player.sendMessage(ChatColor.YELLOW + "[GeyserAdminTools] Perhaps wrong usage ? /gmute <username> <amount of days> <reason>");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return true;
    }
}