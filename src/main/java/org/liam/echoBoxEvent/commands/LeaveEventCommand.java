package org.liam.echoBoxEvent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeManager;
import org.liam.echoBoxEvent.events.spleef.SpleefCommand;
import org.liam.echoBoxEvent.events.spleef.SpleefManager;
import org.liam.echoBoxEvent.events.sumo.SumoManager;
import org.liam.echoBoxEvent.gamestates.GameState;

public class LeaveEventCommand implements CommandExecutor {

    private final EventManager eventManager;
    private final SumoManager sumoManager;
    private final SpleefManager spleefManager;
    private final GlassBridgeManager glassBridgeManager;

    public LeaveEventCommand(EventManager eventManager, SumoManager sumoManager, SpleefManager spleefManager, GlassBridgeManager glassBridgeManager){
        this.eventManager = eventManager;
        this.sumoManager = sumoManager;
        this.spleefManager = spleefManager;
        this.glassBridgeManager = glassBridgeManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (eventManager.isSumoEventActive() && eventManager.isPlayerInEvent(player) && eventManager.getGameState() == GameState.WAITING) {
            sumoManager.leaveSumoEvent(player);
        }else if (eventManager.isSpleefEventActive() && eventManager.isPlayerInEvent(player) && eventManager.getGameState() == GameState.WAITING){
            spleefManager.leaveSpleefEvent(player);
        }else if (eventManager.isGlassBridgeEventActive() && eventManager.isPlayerInEvent(player) && eventManager.getGameState() == GameState.WAITING){
            glassBridgeManager.leaveGlassBridgeEvent(player);
        }
        return true;
    }
}
