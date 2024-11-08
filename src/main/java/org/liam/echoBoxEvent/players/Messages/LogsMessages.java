package org.liam.echoBoxEvent.players.Messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogsMessages {

    public static void send(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("staff")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c(Event Logs) " + message));
            }
        }
    }
}
