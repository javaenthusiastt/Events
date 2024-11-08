package org.liam.echoBoxEvent.commands;

import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeManager;
import org.liam.echoBoxEvent.events.murdermystery.MurderMysteryManager;
import org.liam.echoBoxEvent.events.spleef.SpleefManager;
import org.liam.echoBoxEvent.events.sumo.SumoManager;
import org.liam.echoBoxEvent.Main;

public class JoinEventCommand implements CommandExecutor {

    private final EventManager eventManager;
    private final SpleefManager spleefManager;
    private final SumoManager sumoManager;
    private final MurderMysteryManager murderMysteryManager;
    private final GlassBridgeManager glassBridgeManager;

    public JoinEventCommand(EventManager eventManager, SpleefManager spleefManager, SumoManager sumoManager, MurderMysteryManager murderMysteryManager, GlassBridgeManager glassBridgeManager) {
        this.eventManager = eventManager;
        this.spleefManager = spleefManager;
        this.sumoManager = sumoManager;
        this.murderMysteryManager = murderMysteryManager;
        this.glassBridgeManager = glassBridgeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can join events.");
            return true;
        }

        User user = Main.getEssentials().getUser(player);

        if(user.isJailed()){
            Colors.message(player, "&cYou can't join events while jailed.");
            return true;
        }

        if (!eventManager.isAnyEventActive()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo active events to join at the moment."));
            return true;
        }

        if (eventManager.isSumoEventActive()) {

            sumoManager.joinSumoEvent(player);

        } else if (eventManager.isSpleefEventActive()){

            spleefManager.joinSpleefEvent(player);

        } else if (eventManager.isMurderMysteryEventActive()){

            murderMysteryManager.JoinMurderMysteryEvent(player);

        }else if (eventManager.isGlassBridgeEventActive()){

            glassBridgeManager.joinGlassBridge(player);

        }
        return true;
    }
}

