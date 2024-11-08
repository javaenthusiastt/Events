package org.liam.echoBoxEvent.events.sumo;

import com.earth2me.essentials.User;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.api.LPSupport;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.players.Data.PlayerData;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.*;

public class SumoManager {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final PlayerData playerData;

    public SumoManager(EventManager eventManager, SpawnsManager spawnsManager, PlayerData playerData) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.playerData = playerData;
    }

    public void startSumoEvent(Player player, String mapName) {
        if (eventManager.isAnyEventActive()) {
            return;
        }

        if (!spawnsManager.isSpawnSet("sumo", mapName, "waiting")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError.."));
            return;
        }

        eventManager.resetEvents();
        eventManager.isEventSumo = true;
        eventManager.gameState = GameState.WAITING;

        eventManager.setCurrentMap(mapName);

        eventManager.broadcastEventStart("Sumo");
        eventManager.startJoinCooldown("Sumo");
    }

    public void RandomFight() {
        if (eventManager.participants.size() == 1) {
            Player winner = eventManager.participants.iterator().next();
            playerData.addSumoWins(winner.getUniqueId());
            playerData.addPoints(winner.getUniqueId());
            playerData.addWins(winner.getUniqueId());
            endSumoEvent(winner, null, null);
            return;
        }

        eventManager.battlingPlayers.clear();

        List<Player> participantList = new ArrayList<>(eventManager.participants);
        Collections.shuffle(participantList);

        Player player1 = participantList.get(0);
        Player player2 = participantList.get(1);

        player1.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e&lYour turn!"), ChatColor.translateAlternateColorCodes('&', "&aYou &aVS &a"+player2.getName()), 15,40,15);
        player2.sendTitle(ChatColor.translateAlternateColorCodes('&', "&e&lYour turn!"), ChatColor.translateAlternateColorCodes('&', "&aYou &aVS &a"+player1.getName()), 15,40,15);

        eventManager.battlingPlayers.add(player1);
        eventManager.battlingPlayers.add(player2);
        eventManager.sumoRoundNumber++;

        new BukkitRunnable() {
            @Override
            public void run() {
                String mapName = eventManager.getCurrentMap();

                Location One = spawnsManager.getSpawn("sumo", mapName, "fightlocation1");
                Location Two = spawnsManager.getSpawn("sumo", mapName, "fightlocation2");

                if (One != null && Two != null) {
                    player1.teleport(One);
                    player2.teleport(Two);


                    eventManager.frozenPlayers.add(player1);
                    eventManager.frozenPlayers.add(player2);

                    new BukkitRunnable() {
                        int countdown = 3;

                        @Override
                        public void run() {
                            if (countdown > 0) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&l► Round &e&l" + eventManager.sumoRoundNumber + " &bwill start in &e&l" + countdown + "&b!"));
                                countdown--;
                                for (Player sumoPlayers : eventManager.participants) {
                                    sumoPlayers.playSound(sumoPlayers, Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1f);
                                }
                            } else {
                                eventManager.frozenPlayers.remove(player1);
                                eventManager.frozenPlayers.remove(player2);
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("EchoBoxEvent")), 0L, 20L);
                } else {
                    cancelSumoEvent();
                }
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("EchoBoxEvent")), 60L);
    }

    public void endSumoEvent(Player winner, Player second, Player third) {
        winner.setHealth(20f);
        winner.setFoodLevel(20);
        winner.setSaturation(20f);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + winner.getName() + " 2000");

        World world = Bukkit.getWorld("world");
        if (world != null) {
            Location customSpawnLocation = new Location(world, 773.472, 61.0, -80.483);

            User user = Main.getEssentials().getUser(winner);
            if (user != null) {
                user.setLastLocation(customSpawnLocation);
            }
        }

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

        cancelSumoEvent();
    }

    public void cancelSumoEvent() {
        for (Player participant : eventManager.participants) {
            InventoryHolderManager.restorePlayerInventory(participant);
            participant.performCommand("spawn");
        }

        eventManager.participants.clear();
        eventManager.battlingPlayers.clear();

        eventManager.resetEvents();
    }

    public void sumo() {
        Location sumoArenaSpawn = spawnsManager.getSpawn("sumo", eventManager.getCurrentMap(),"arena");

        if (sumoArenaSpawn == null) {
            return;
        }

        for (Player participant : eventManager.participants) {
            participant.teleport(sumoArenaSpawn);
        }
    }

    public void handlePlayerLeaveDuringFight(Player player) {
        if (eventManager.battlingPlayers.contains(player)) {
            Player remainingPlayer = eventManager.battlingPlayers.get(0).equals(player) ? eventManager.battlingPlayers.get(1) : eventManager.battlingPlayers.get(0);

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + remainingPlayer.getName() + " &7won the round because &c" + player.getName() + " &7left (&c"+eventManager.participants.size()+"&7/&c"+eventManager.Max_Players_For_Sumo+"&7)"));

            eventManager.battlingPlayers.clear();

            Location sumoArenaSpawn = spawnsManager.getSpawn("sumo", eventManager.getCurrentMap(), "arena");

            if (sumoArenaSpawn == null) {
                return;
            }

            remainingPlayer.teleport(sumoArenaSpawn);

            if (eventManager.participants.size() > 2) {
                RandomFight();
            } else {
                Player winner = eventManager.getParticipants().iterator().next();
                endSumoEvent(winner, null, null);
            }
        }

        eventManager.participants.remove(player);
    }

    public void leaveSumoEvent(Player player) {
        if (eventManager.battlingPlayers.contains(player)) {
            handlePlayerLeaveDuringFight(player);
        } else {
            eventManager.participants.remove(player);

            InventoryHolderManager.restorePlayerInventory(player);
            player.performCommand("spawn");

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&7 has left the Sumo event. &7(&c"+eventManager.participants.size()+"&7/&c"+eventManager.Max_Players_For_Sumo+"&7)"));
        }
    }

    public Set<Player> getFrozenPlayers() {
        return eventManager.frozenPlayers;
    }

    public void joinSumoEvent(Player player) {
        if (eventManager.gameState != GameState.WAITING) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNot in the right &dstate&e to join event"));
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

        if(eventManager.participants.contains(player)){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are already in the event."));
            return;
        }

        if (eventManager.participants.size() >= eventManager.Max_Players_For_Sumo) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eEvent is &dfull"));
            return;
        }

        Location waiting = spawnsManager.getSpawn("sumo", eventManager.getCurrentMap(), "waiting");

        if (waiting == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError: Sumo waiting spawn location not found."));
            return;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l► &aAdding you to the sumo queue.."));

        eventManager.participants.add(player);

        if (player.getName().equalsIgnoreCase("sorryplspls")) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', "&c&l" + player.getName() + " &ejoined the fight &e(&b" + eventManager.participants.size() + "&e/&b" + eventManager.Max_Players_For_Sumo + "&e)!"));
        } else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', "&7" + player.getName() + " &ejoined the fight &e(&b" + eventManager.participants.size() + "&e/&b" + eventManager.Max_Players_For_Sumo + "&e)!"));
        }

        playerData.addSumoPlayed(player.getUniqueId());
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

                ItemStack knockbackStick = new ItemStack(Material.STICK);
                ItemMeta knockbackMeta = knockbackStick.getItemMeta();

                Objects.requireNonNull(knockbackMeta).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSumo Stick"));
                knockbackMeta.setLore(List.of("", ChatColor.GREEN + "Knockback 2", ""));
                knockbackMeta.addEnchant(Enchantment.KNOCKBACK, 2, true);
                knockbackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                knockbackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                knockbackStick.setItemMeta(knockbackMeta);

                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                player.getInventory().setItem(0, knockbackStick);

                player.teleport(waiting);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&C&lNOTE: &aThis event-type is still currently under developement, which means bugs can occur and we'd like to hear your feedback! Join our discord with /discord and say what you think about it so far! :)"));
                player.playEffect(EntityEffect.TOTEM_RESURRECT);

            }
        }.runTaskLater(Main.getPluginInstance(), 15L);
    }

    public List<Player> getBattlingPlayers() {
        return eventManager.battlingPlayers;
    }
}
