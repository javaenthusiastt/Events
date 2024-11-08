package org.liam.echoBoxEvent.setup;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.maps.SpleefMapReset;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

public class EventAdminCommand implements CommandExecutor {

    private final SpawnsManager spawnsManager;

    public EventAdminCommand(SpawnsManager spawnsManager) {
        this.spawnsManager = spawnsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("echobox.event.admin")) {
            player.sendMessage(ChatColor.RED + "Insufficient permissions.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("resetmap")) {
            SpleefMapReset.ResetMap();
            player.sendMessage(ChatColor.GREEN + "Map has been reset.");
            return true;
        }

        if (args.length < 3) {
            sendUsageMessage(player);
            return true;
        }

        String eventType = args[0].toLowerCase();
        String mapName = args[1];
        String action = args[2].toLowerCase();

        switch (eventType) {
            case "sumo" -> handleEventCommand(player, mapName, action, "sumo", new String[]{"create", "waiting", "arena", "fightlocation1", "fightlocation2"});
            case "hideandseek" -> handleEventCommand(player, mapName, action, "hideandseek", new String[]{"create", "seekerwait", "hiderswait"});
            case "spleef" -> handleEventCommand(player, mapName, action, "spleef", new String[]{"create", "waiting", "arena", "randomspawner1", "randomspawner2"});
            case "murdermystery" -> handleEventCommand(player, mapName, action, "murdermystery", new String[]{"create", "waiting", "murdererspawn", "innocentspawn"});
            case "glassbridge" -> handleEventCommand(player, mapName, action, "glassbridge", new String[]{"create", "waiting", "arena"});
            case "icerace", "quests", "lobby" -> player.sendMessage(ChatColor.YELLOW + "Event type " + eventType + " is not yet implemented.");
            default -> sendUsageMessage(player);
        }

        return true;
    }

    private void handleEventCommand(Player player, String mapName, String action, String eventType, String[] validActions) {
        if (!isValidAction(action, validActions)) {
            player.sendMessage(ChatColor.RED + "Invalid action. Available actions for " + eventType + ": " + String.join(", ", validActions));
            return;
        }

        if (action.equals("create")) {
            spawnsManager.createMap(eventType, mapName);
            player.sendMessage(ChatColor.GREEN + "Created new " + eventType + " map: " + mapName);
        } else {
            if (!spawnsManager.isMapCreated(eventType, mapName)) {
                player.sendMessage(ChatColor.RED + "Map " + mapName + " does not exist. Please create it first with /eventadmin " + eventType + " " + mapName + " create");
                return;
            }
            setSpawnLocation(player, eventType, mapName, action);
        }
    }

    private boolean isValidAction(String action, String[] validActions) {
        for (String validAction : validActions) {
            if (validAction.equalsIgnoreCase(action) || action.matches("randomspawner[1-9][0-9]*")) {
                return true;
            }
        }
        return false;
    }

    private void setSpawnLocation(Player player, String eventType, String mapName, String spawnType) {
        Location location = player.getLocation();
        spawnsManager.setSpawn(eventType, mapName, spawnType, location);
        player.sendMessage(ChatColor.GREEN + eventType + " spawn " + spawnType + " set for map " + mapName);
    }

    private void sendUsageMessage(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Usage: /eventadmin <sumo|hideandseek|spleef|icerace|murdermystery|quests|lobby> <map/quest/particles> <actions/skull/give/sethere>");
        player.sendMessage(ChatColor.YELLOW + "Examples:");
        player.sendMessage(ChatColor.YELLOW + " - /eventadmin sumo <map> <create|waiting|arena|fightlocation1|fightlocation2>");
        player.sendMessage(ChatColor.YELLOW + " - /eventadmin glassbridge <map> <create|waiting|arena>");
    }
}
