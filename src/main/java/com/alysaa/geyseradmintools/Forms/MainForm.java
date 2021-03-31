package com.alysaa.geyseradmintools.Forms;

import com.alysaa.geyseradmintools.Gat;
import com.alysaa.geyseradmintools.utils.CheckJavaOrFloodPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class MainForm {
    @SuppressWarnings("deprecation")
    public static void formList(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
            if (isFloodgatePlayer) {
                FloodgatePlayer fplayer = FloodgateApi.getInstance().getPlayer(uuid);
                fplayer.sendForm(
                        SimpleForm.builder()
                                .title("AdminTools")
                                .content("List of Tools")
                                .button("Player Tools")
                                .button("Mobs Tools")
                                .button("Server Tools")
                                .button("Remove all gadmin effects")
                                .responseHandler((form, responseData) -> {
                                    SimpleFormResponse response = form.parseResponse(responseData);
                                    if (!response.isCorrect()) {
                                        // player closed the form or returned invalid info (see FormResponse)
                                        return;
                                    }
                                    if (response.getClickedButtonId() == 0) {
                                        if (Gat.plugin.getConfig().getBoolean("Forms.EnablePlayerForm")) {
                                            new PlayerToolsForm().PTList();
                                        } else {
                                            player.sendMessage("This form has been disabled");
                                        }
                                        if (response.getClickedButtonId() == 1) {
                                            if (Gat.plugin.getConfig().getBoolean("Forms.EnableMobForm")) {
                                                new MobsToolsForm().preMTList();
                                            } else {
                                                player.sendMessage("This form has been disabled");
                                            }
                                            if (response.getClickedButtonId() == 2) {
                                                if (Gat.plugin.getConfig().getBoolean("Forms.EnableServerForm")) {
                                                    new ServerToolsForm().STList();
                                                } else {
                                                    player.sendMessage("This form has been disabled");
                                                }
                                                if (response.getClickedButtonId() == 2) {
                                                    player.setInvulnerable(false);
                                                    player.setAllowFlight(false);
                                                    player.setGameMode(GameMode.SURVIVAL);
                                                    player.showPlayer(player);
                                                }
                                            }
                                        }
                                    }
                                }));
            }else {
                player.sendMessage("Sorry this is a Bedrock command!");
            }
        }
    }
}