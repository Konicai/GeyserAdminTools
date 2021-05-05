package com.projectg.geyseradmintools.forms;

import com.projectg.geyseradmintools.utils.CheckJavaOrFloodPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Objects;
import java.util.UUID;

import static com.projectg.geyseradmintools.listeners.AdminToolChat.isMuted;

public class ServerToolsForm {
    public void STList(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
        if (isFloodgatePlayer) {
            FloodgatePlayer fplayer = FloodgateApi.getInstance().getPlayer(uuid);
            fplayer.sendForm(
                    SimpleForm.builder()
                            .title("Server Tools")
                            .content("List of server tools")
                            .button("Weather Sun")//1
                            .button("Weather Rain")//2
                            .button("Day")//3
                            .button("Night")//4
                            .button("Clear Chat")
                            .button("Lock Chat")
                            .button("Unlock Chat")
                            .responseHandler((form, responseData) -> {
                                SimpleFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    // player closed the form or returned invalid info (see FormResponse)
                                    return;
                                }
                                if (response.getClickedButtonId() == 0) {
                                    player.getWorld().setStorm(false);
                                    player.getWorld().setWeatherDuration(18000);
                                    player.sendMessage(ChatColor.GREEN + "[GeyserAdminTools] Weather set on Sunny");
                                }
                                if (response.getClickedButtonId() == 1) {
                                    player.getWorld().setStorm(true);
                                    player.getWorld().setWeatherDuration(18000);
                                    player.sendMessage(ChatColor.GREEN + "[GeyserAdminTools] Weather set on Rain");
                                }
                                if (response.getClickedButtonId() == 2) {
                                    player.getWorld().setTime(1000);
                                    player.sendMessage(ChatColor.GREEN + "[GeyserAdminTools] Time set on Day");
                                }
                                if (response.getClickedButtonId() == 3) {
                                    Objects.requireNonNull(Bukkit.getWorld("world")).setTime(14000);
                                    player.sendMessage(ChatColor.GREEN + "[GeyserAdminTools] Time set on Night");
                                }
                                if (response.getClickedButtonId() == 4) {
                                    int i;
                                    for (i = 0; i < 25; i++) {
                                        player.sendMessage(" ");
                                    }
                                    Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Chat has been cleared by an admin!");
                                }
                                if (response.getClickedButtonId() == 5) {
                                    if (player.hasPermission("geyseradmintools.gadmin")) {
                                        if (!isMuted) {
                                            // Disables chat
                                            isMuted = true;
                                            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Chat has been locked by an admin! ");
                                        }
                                    }
                                }
                                if (response.getClickedButtonId() == 6) {
                                    if (player.hasPermission("geyseradmintools.gadmin")) {
                                        if (!isMuted) {
                                            // Disables chat
                                            Bukkit.broadcastMessage(ChatColor.GREEN + "Chat has been unlocked by an admin! ");
                                        }
                                    }
                                }
                            }));
        }
    }
}