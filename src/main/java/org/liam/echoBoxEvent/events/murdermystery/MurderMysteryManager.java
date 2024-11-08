package org.liam.echoBoxEvent.events.murdermystery;

import com.earth2me.essentials.User;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MurderMysteryManager {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;

    public HashSet<Player> murdererSet = new HashSet<>();
    public HashSet<Player> innocentSet = new HashSet<>();

    public MurderMysteryManager(EventManager eventManager, SpawnsManager spawnsManager) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
    }

    public void startMurderMysteryEvent(Player player, String mapName){
        if(eventManager.isAnyEventActive()) return;

        if(!(spawnsManager.isSpawnSet("murdermystery", mapName, "waiting"))){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError.."));
            return;
        }

        eventManager.resetEvents();
        eventManager.isEventMurderMystery = true;
        eventManager.gameState = GameState.WAITING;
        eventManager.setCurrentMap(mapName);
        eventManager.broadcastEventStart("Murder Mystery");
        eventManager.startJoinCooldown("Murder Mystery");
    }

    public void cancelMurderMystery() {
        for (Player participant : eventManager.participants) {
            InventoryHolderManager.restorePlayerInventory(participant);
            participant.performCommand("spawn");
        }

        eventManager.participants.clear();
        eventManager.battlingPlayers.clear();
        eventManager.resetEvents();
    }

    public void JoinMurderMysteryEvent(Player player){
        if(eventManager.gameState != GameState.WAITING){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNot in the right &dstate&e to join event"));
            return;
        }

        User user = Main.getEssentials().getUser(player);

        if(user.isVanished()){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot join while vanished."));
            return;
        }

        if(user.isGodModeEnabled()){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou cannot join while god moded."));
            return;
        }

        if (eventManager.participants.size() >= eventManager.Max_Players_For_MurderMystery) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&eEvent is &dfull"));
            return;
        }


        Location murderMysteryWaitingSpawn = spawnsManager.getSpawn("murdermystery", eventManager.getCurrentMap(), "waiting");

        if(murderMysteryWaitingSpawn == null){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError"));
            return;
        }

        if(eventManager.participants.contains(player)){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already in the event."));
            return;
        }

        eventManager.participants.add(player);
        player.teleport(murderMysteryWaitingSpawn);

        player.setHealth(20f);
        player.setSaturation(20f);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);

        if(player.getAllowFlight()){
            player.setAllowFlight(!player.getAllowFlight());
        }

        player.getActivePotionEffects().forEach(potionEffect -> {
            if (potionEffect.getType() != PotionEffectType.NIGHT_VISION) {
                player.removePotionEffect(potionEffect.getType());
            }
        });

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);

        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lMURDER MYSTERY"), ChatColor.translateAlternateColorCodes('&', "&7Welcome, &4"+player.getName()), 15, 80, 30);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);

        InventoryHolderManager.saveInventory(player);

        if(player.isGlowing()){
            player.setGlowing(false);
        }

        if(player.getName().equalsIgnoreCase("sorryplspls")){
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', ChatColor.translateAlternateColorCodes('&',
                            "&c"+player.getName()+" &ejoined the mystery &e(&b"+eventManager.participants.size()+"&e/&b"+eventManager.Max_Players_For_MurderMystery+"&e)!")));
        }else{
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', ChatColor.translateAlternateColorCodes('&',
                            "&7"+player.getName()+" &ejoined the mystery &e(&b"+eventManager.participants.size()+"&e/&b"+eventManager.Max_Players_For_MurderMystery+"&e)!")));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (eventManager.gameState != GameState.WAITING || !eventManager.participants.contains(player)) {
                    this.cancel();
                    return;
                }

                int participants = eventManager.participants.size();
                double murdererChance = (1.0 / participants) * 100;

                String chanceMessage = ChatColor.translateAlternateColorCodes('&', "&cMurderer Chance: &c" + String.format("%.2f", murdererChance) + "%");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chanceMessage));
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 40L);

    }

    public void MurderMystery(){
        List<Player> participants = new ArrayList<>(eventManager.participants);
        Random random = new Random();
        Player murderer = participants.get(random.nextInt(participants.size()));

        innocentSet.clear();
        murdererSet.clear();

        murdererSet.add(murderer);

        murderer.sendTitle(
                ChatColor.translateAlternateColorCodes('&', "&cMurderer"),
                ChatColor.translateAlternateColorCodes('&', "&6Kill all the innocents to win!"),
                25,30,25
        );

        Colors.message(murderer, "&c&l➤ Murderer");
        murderer.sendMessage("");
        Colors.message(murderer, "&c&l➤ &6Kill all the innocents to win! &7Gain kills for extra beacons.");

        murderer.playSound(murderer, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2f, 0.7f);

        for (Player player : participants) {
            if (!player.equals(murderer)) {
                innocentSet.add(player);
                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', "&aInnocent"),
                        ChatColor.translateAlternateColorCodes('&', "&bTry your best to survive!"),
                        25,30,25
                );

                Colors.message(player, "&a&l➤ Innocent");
                player.sendMessage("");
                Colors.message(player, "&a&l➤ &bTry your best to survive!");


                player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
            }
        }
    }
}
