package org.liam.echoBoxEvent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.Main;

public class InformationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        Colors.message(player, "&cEvents plugin made by &7sorryplspls &cfor Echobox.");
        Colors.message(player, "&cRunning version: &7"+Main.getPluginInstance().getServer().getVersion());
        Colors.message(player, "&cRunning plugin version: &7"+Main.getPluginInstance().getDescription().getVersion());
        Colors.message(player, "&cRunning API version: &7"+Main.getPluginInstance().getDescription().getAPIVersion());
        return false;
    }
}
