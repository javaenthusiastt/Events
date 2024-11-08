package org.liam.echoBoxEvent.events.glassbridge;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.List;

public class GlassBridgeCommand implements CommandExecutor {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final GlassBridgeManager glassBridgeManager;

    public GlassBridgeCommand(EventManager eventManager, SpawnsManager spawnsManager, GlassBridgeManager glassBridgeManager) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.glassBridgeManager = glassBridgeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        if(!player.getName().equalsIgnoreCase("sorryplspls")){
            Colors.message(player, "&cThis is under developement.");
            return true;
        }

        /*if(!(player.hasPermission("echobox.event.host"))){
            Colors.message(player, "&cNo permission.");
            return true;
        }*/

        if (args.length == 0) {
            List<String> availableMaps = spawnsManager.getAvailableMaps("glassbridge");

            if (availableMaps.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo available maps for this Event ID."));
                return true;
            } else {
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hello, &6"+player.getName()+"&7! Let's make a event, select the map you'd like to play and it'll start!"));
                player.sendMessage("");
                for (String map : availableMaps) {
                    TextComponent mapMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7Play on &2" + map + " &a(Click)"));
                    mapMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/echoboxglassbridge " + map));
                    mapMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', "&7&l↘ &2"+map+" &7&l↙"))));
                    player.spigot().sendMessage(mapMessage);
                }
            }
            return true;
        }

        String mapName = args[0];

        if (!spawnsManager.getAvailableMaps("glassbridge").contains(mapName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMap &d" + mapName + " &edoes not exist. Use &d/echoboxglassbridge &eto see available maps."));
            return true;
        }

        if(!(eventManager.isAnyEventActive())){
            glassBridgeManager.startGlassBridge(mapName);
        }else{
            Colors.message(player, "&cAnother event is currently being held.");
        }
        return true;

    }
}
