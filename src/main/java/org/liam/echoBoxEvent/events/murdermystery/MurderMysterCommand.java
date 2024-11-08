package org.liam.echoBoxEvent.events.murdermystery;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.List;

public class MurderMysterCommand implements CommandExecutor {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final MurderMysteryManager murderMysteryManager;

    public MurderMysterCommand(EventManager eventManager, SpawnsManager spawnsManager, MurderMysteryManager murderMysteryManager) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.murderMysteryManager = murderMysteryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

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
            List<String> availableMaps = spawnsManager.getAvailableMaps("murdermystery");

            if (availableMaps.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo available maps for this Event ID."));
                return true;
            } else {
                Colors.message(player, "&7Choose a map!");
                for (String map : availableMaps) {
                    TextComponent mapMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&4- &c" + map + " &e(Click)"));
                    mapMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/echoboxmurdermystery " + map));
                    mapMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', "&7Click to start &4Murder Mystery &7on the map &c"+map))));
                    player.spigot().sendMessage(mapMessage);
                }
            }
            return true;
        }

        String mapName = args[0];

        if (!spawnsManager.getAvailableMaps("murdermystery").contains(mapName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMap &d" + mapName + " &edoes not exist. Use &d/echoboxmurdermystery &eto see available maps."));
            return true;
        }

        if(!(eventManager.isAnyEventActive())){
            murderMysteryManager.startMurderMysteryEvent(player, mapName);
        }else{
            Colors.message(player, "&cAnother event is currently being held.");
        }
        return true;
    }
}
