package org.liam.echoBoxEvent.colors;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Colors {

    public static void message(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}