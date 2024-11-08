package org.liam.echoBoxEvent.events.sumo;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.List;

public class SumoCommand implements CommandExecutor {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final SumoManager sumoManager;

    private static long lastCommandTime = 0;
    private static final long COOLDOWN_TIME = 10 * 60 * 1000;

    public SumoCommand(EventManager eventManager, SpawnsManager spawnsManager, SumoManager sumoManager) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.sumoManager = sumoManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if(!player.getName().equalsIgnoreCase("sorryplspls")){
            Colors.message(player, "&cThis is under developement.");
            return true;
        }

        if (!player.hasPermission("echobox.event.host")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo permission."));
            return true;
        }

        if (args.length == 0) {
            List<String> availableMaps = spawnsManager.getAvailableMaps("sumo");

            if (availableMaps.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo available maps for this Event ID."));
                return true;
            } else {
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hello, &6"+player.getName()+"&7! Let's make a event, select the map you'd like to play and it'll start!"));
                player.sendMessage("");
                for (String map : availableMaps) {
                    TextComponent mapMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7Play on &2" + map + " &a(Click)"));
                    mapMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/echoboxsumo " + map));
                    mapMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', "&7&l↘ &2"+map+" &7&l↙"))));
                    player.spigot().sendMessage(mapMessage);
                }
            }
            return true;
        }

        if (!player.isOp()) {
            long timeSinceLastCommand = System.currentTimeMillis() - lastCommandTime;
            if (timeSinceLastCommand < COOLDOWN_TIME) {
                long timeLeft = (COOLDOWN_TIME - timeSinceLastCommand) / 1000;
                long minutesLeft = timeLeft / 60;
                long secondsLeft = timeLeft % 60;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&eYou need to wait &d" + minutesLeft + " &eminutes and &d" + secondsLeft + " &eseconds before starting a new &devent"));
                return true;
            }
        }

        String mapName = args[0];

        if (!spawnsManager.getAvailableMaps("sumo").contains(mapName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMap &d" + mapName + " &edoes not exist. Use &d/echoboxsumo &eto see available maps."));
            return true;
        }

        if (!eventManager.isAnyEventActive()) {
            sumoManager.startSumoEvent(player, mapName);
            lastCommandTime = System.currentTimeMillis();
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eAnother &devent &eis currently being held."));
        }

        return true;
    }
}
