package com.alysaa.geyseradmintools.forms;

import com.alysaa.geyseradmintools.utils.CheckJavaOrFloodPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeleportForm {
    public static void tpPlayer(Player player){
        Runnable runnable = () -> {
        UUID uuid = player.getUniqueId();
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        String[] playerlist = names.toArray(new String[0]);
        boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
        if (isFloodgatePlayer) {
            FloodgatePlayer fplayer = FloodgateApi.getInstance().getPlayer(uuid);
            fplayer.sendForm(
                    CustomForm.builder()
                            .title("Teleport to player")
                            .dropdown("Select Player", playerlist)
                            .responseHandler((form, responseData) -> {
                                CustomFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    return;
                                }
                                int clickedIndex = response.getDropdown(0);
                                String name = names.get(clickedIndex);
                                Player player1 = Bukkit.getPlayer(name);
                                assert player1 != null;
                                player.teleport(player1);
                            }));
        }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}