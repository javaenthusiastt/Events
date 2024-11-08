package org.liam.echoBoxEvent.maps;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.players.Messages.LogsMessages;

import java.util.HashMap;
import java.util.Map;

public class SpleefMapReset implements Listener {

    public static final Map<Location, Material> defaultBlocks = new HashMap<>();
    private final EventManager eventManager;

    public SpleefMapReset(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @EventHandler
    public void PlayerBreakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(eventManager.isPlayerInEvent(player)){
            if(!(eventManager.isSpleefEventActive())){
                return;
            }else{
                if(eventManager.getGameState() == GameState.IN_PROGRESS){
                    Block block = event.getBlock();
                    defaultBlocks.putIfAbsent(block.getLocation(), block.getType());
                }
            }
        }
    }

    public static void ResetMap() {
        if (!defaultBlocks.isEmpty()) {
            for (Map.Entry<Location, Material> entry : defaultBlocks.entrySet()) {
                Location loc = entry.getKey();
                Material originalMaterial = entry.getValue();

                loc.getBlock().setType(originalMaterial);
            }
            LogsMessages.send("&a&lSUCCESS");
            LogsMessages.send("&7Block data found, restored &c"+defaultBlocks.size()+" &7blocks.");
            defaultBlocks.clear();
        }else{
            LogsMessages.send("&c&lFAILED");
            LogsMessages.send("&cError: &7Tried to restore &cblock data&7, but none found.");
        }
    }

}
