package org.liam.echoBoxEvent.events.glassbridge;

import com.earth2me.essentials.User;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.api.LPSupport;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.maps.GlassBridgeMapReset;
import org.liam.echoBoxEvent.players.Data.PlayerData;
import org.liam.echoBoxEvent.spawns.SpawnsManager;


public class GlassBridgeManager {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final PlayerData playerData;

    public GlassBridgeManager(EventManager eventManager, SpawnsManager spawnsManager, PlayerData playerData) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.playerData = playerData;
    }

    public void startGlassBridge(String mapName){
        if(eventManager.isAnyEventActive()) return;

        if (!spawnsManager.isSpawnSet("glassbridge", mapName, "waiting")) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cError occur. Contact developer"));
            return;
        }

        eventManager.resetEvents();
        eventManager.isEventGlassBridge = true;
        eventManager.gameState = GameState.WAITING;

        eventManager.setCurrentMap(mapName);

        eventManager.broadcastEventStart("Glass Bridge");
        eventManager.startJoinCooldown("Glass Bridge");
    }

    public void cancelGlassBridge(){
        for (Player participant : eventManager.participants) {
            InventoryHolderManager.restorePlayerInventory(participant);
            participant.performCommand("spawn");
        }

        GlassBridgeMapReset.resetGreenBlocks();
        GlassBridgeMapReset.resetRedBlocks();
        eventManager.participants.clear();
        eventManager.resetEvents();
    }

    public void joinGlassBridge(Player player) {
        if (eventManager.gameState != GameState.WAITING) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Not in the right &cstate&7 to join event"));
            return;
        }

        User user = Main.getEssentials().getUser(player);

        if (user.isVanished()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot join while vanished."));
            return;
        }

        if (user.isGodModeEnabled()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot join while god moded."));
            return;
        }


        if (eventManager.participants.size() >= eventManager.Max_Players_For_GlassBridge) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eEvent is &dfull"));
            return;
        }

        if (eventManager.participants.contains(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You are already in the &cevent"));
            return;
        }

        Location waiting = spawnsManager.getSpawn("glassbridge", eventManager.getCurrentMap(), "waiting");

        if (waiting == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError"));
            return;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l► &aAdding you to the Glass-Bridge queue.."));

        eventManager.participants.add(player);

        playerData.addGlassBridgeGames(player.getUniqueId());
        playerData.addPoints(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {

                InventoryHolderManager.saveInventory(player);

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.getInventory().setItemInOffHand(null);

                player.setHealth(20f);
                player.setSaturation(20f);
                player.setFoodLevel(20);
                player.setGameMode(GameMode.SURVIVAL);
                player.setAllowFlight(false);

                player.getActivePotionEffects().forEach(potionEffect -> {
                    if (potionEffect.getType() != PotionEffectType.NIGHT_VISION) {
                        player.removePotionEffect(potionEffect.getType());
                    }
                });

                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);

                player.teleport(waiting);

                if (player.getName().equalsIgnoreCase("sorryplspls")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                            '&', "&c&l" + player.getName() + " &ejoined the bridge &e(&b" + eventManager.participants.size() + "&e/&b" + eventManager.Max_Players_For_GlassBridge + "&e)!"));
                } else {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                            '&', "&7" + player.getName() + " &ejoined the bridge &e(&b" + eventManager.participants.size() + "&e/&b" + eventManager.Max_Players_For_GlassBridge + "&e)!"));
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&C&lNOTE: &aThis event-type is still currently under developement, which means bugs can occur and we'd like to hear your feedback! Join our discord with /discord and say what you think about it so far! :)"));
                player.playEffect(EntityEffect.TOTEM_RESURRECT);
            }
        }.runTaskLater(Main.getPluginInstance(), 40L);
    }

    public void leaveGlassBridgeEvent(Player player){
        eventManager.participants.remove(player);
        InventoryHolderManager.restorePlayerInventory(player);
        player.performCommand("spawn");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&7 has left the Glass Bridge event. &7(&c" + eventManager.participants.size() + "&7/&c" + eventManager.Max_Players_For_GlassBridge + "&7)"));
    }

    public void Start(){
        for(Player player : eventManager.getParticipants()){
            Location arena = spawnsManager.getSpawn("glassbridge", eventManager.getCurrentMap(),"arena");
            if(arena != null){
                player.teleport(arena);
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lHOW TO PLAY"));
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You'll have to take a chance to jump on one of the glasses and hope they do not break!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If the glass break, &c&lYOUR OUT &7and will be eliminated from the event."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Only &A&LONE &7player can be at the glass and take their chances and everyone else need to wait."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Either the player jumping will guess wrong, and all blocks will be resetted or they take right and everyone know its the right block."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Work like a team, but don't since it's the first person to the other side to push the button."));
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lGOODLUCK."));
                player.sendMessage("");
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
            }
        }
    }

    public void endGlassBridgeEvent(Player winner, Player second, Player third) {
        winner.setHealth(20.0);
        winner.setFoodLevel(20);
        winner.setSaturation(20.0f);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + winner.getName() + " 2000");

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂"));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "              &f&LGLASS BRIDGE"));
        Bukkit.broadcastMessage("");

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "            &e&l1st Place: " + LPSupport.getLpPrefix(winner.getUniqueId()) + winner.getName()));

        String secondPlace = (second != null) ? LPSupport.getLpPrefix(second.getUniqueId()) + second.getName() : "&7N/A";
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "            &e&l2nd Place: " + secondPlace));

        String thirdPlace = (third != null) ? LPSupport.getLpPrefix(third.getUniqueId()) + third.getName() : "&7N/A";
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "            &e&l3rd Place: " + thirdPlace));

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂"));

        cancelGlassBridge();
    }
}
