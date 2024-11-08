package org.liam.echoBoxEvent.events.spleef;

import com.earth2me.essentials.User;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.maps.SpleefMapReset;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.*;

public class SpleefManager {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;

    private BukkitTask timerTask;

    public final Set<Player> frozenPlayers = new HashSet<>();

    public SpleefManager(EventManager eventManager, SpawnsManager spawnsManager) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
    }

    public void startSpleefEvent(Player player, String mapName) {
        if (eventManager.isAnyEventActive()) {
            return;
        }

        if (!spawnsManager.isSpawnSet("spleef", mapName, "waiting")) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cError occur. Contact developer"));
            return;
        }

        eventManager.resetEvents();
        eventManager.isEventSpleef = true;
        eventManager.gameState = GameState.WAITING;

        eventManager.setCurrentMap(mapName);

        eventManager.broadcastEventStart("Spleef");
        eventManager.startJoinCooldown("Spleef");
    }

    public void cancelSpleefEvent() {
        for (Player participant : eventManager.participants) {
            InventoryHolderManager.restorePlayerInventory(participant);
            participant.performCommand("spawn");
        }

        eventManager.participants.clear();
        SpleefMapReset.ResetMap();
        eventManager.resetEvents();

        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public void joinSpleefEvent(Player player){
        if (eventManager.gameState != GameState.WAITING) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Not in the right &cstate&7 to join event"));
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


        if (eventManager.participants.size() >= eventManager.Max_Players_For_Spleef) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eEvent is &dfull"));
            return;
        }

        Location waiting = spawnsManager.getSpawn("spleef", eventManager.getCurrentMap(), "waiting");

        if (waiting == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&cError"));
            return;
        }

        if (eventManager.participants.contains(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You are already in the &cevent"));
            return;
        }

        eventManager.participants.add(player);
        player.teleport(waiting);

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

        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);


        ItemStack spleefShovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta shovelMeta = spleefShovel.getItemMeta();

        assert shovelMeta != null;

        shovelMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&k&lii &d&lSPLEEF SHOVEL &k&lii"));
        shovelMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);

        shovelMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        shovelMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        shovelMeta.setLore(List.of("", ChatColor.translateAlternateColorCodes('&', "&e&l◆ &dShovel For Spleef!"), ""));
        spleefShovel.setItemMeta(shovelMeta);

        InventoryHolderManager.saveInventory(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);


        player.getInventory().setItem(0, spleefShovel);

        if(player.getName().equalsIgnoreCase("sorryplspls")){
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', ChatColor.translateAlternateColorCodes('&',
                            "&c"+player.getName()+" &ejoined with their shovel &e(&b"+eventManager.participants.size()+"&e/&b"+eventManager.Max_Players_For_Spleef+"&e)!")));
        }else{
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
                    '&', ChatColor.translateAlternateColorCodes('&',
                            "&7"+player.getName()+" &ejoined with their shovel &e(&b"+eventManager.participants.size()+"&e/&b"+eventManager.Max_Players_For_Spleef+"&e)!")));
        }
    }

    public void Spleef() {
        String mapName = eventManager.getCurrentMap();

        List<String> spawnerKeys = spawnsManager.mapKeysGetter("spleef", mapName);

        List<Location> spawnerLocations = new ArrayList<>();
        for (String spawnerKey : spawnerKeys) {
            if (spawnerKey.startsWith("randomspawner")) {
                Location location = spawnsManager.getSpawn("spleef", mapName, spawnerKey);
                if (location != null) {
                    spawnerLocations.add(location);
                }
            }
        }

        if (spawnerLocations.isEmpty()) {
            cancelSpleefEvent();
            return;
        }

        List<Player> participants = new ArrayList<>(eventManager.getParticipants());

        for (int i = 0; i < participants.size(); i++) {
            Player participant = participants.get(i);
            Location spawnLocation = spawnerLocations.get(i % spawnerLocations.size());
            participant.teleport(spawnLocation);
            frozenPlayers.add(participant);
        }

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    String colorCode;
                    switch (countdown) {
                        case 5 -> colorCode = "&e&l➄";
                        case 4 -> colorCode = "&6&l➃";
                        case 3 -> colorCode = "&c&l➂";
                        case 2 -> colorCode = "&d&l➁";
                        case 1 -> colorCode = "&4&l➀";
                        default -> colorCode = "&c";
                    }

                    for (Player participant : eventManager.getParticipants()) {
                        participant.playSound(participant, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.3f);
                        participant.sendTitle(
                                ChatColor.translateAlternateColorCodes('&', colorCode),
                                ChatColor.translateAlternateColorCodes('&', "&7until start"),
                                10, 20, 10
                        );
                    }
                    countdown--;
                } else {
                    frozenPlayers.clear();
                    this.cancel();
                    Timer();
                    for(Player participant : eventManager.getParticipants()){
                        participant.setGlowing(true);
                    }
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 20L);
    }

    public void leaveSpleefEvent(Player player) {
        eventManager.participants.remove(player);
        InventoryHolderManager.restorePlayerInventory(player);
        player.performCommand("spawn");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + "&7 has left the Spleef event. &7(&c" + eventManager.participants.size() + "&7/&c" + eventManager.Max_Players_For_Spleef + "&7)"));
    }

    public void checkWinnerSpleef() {
        if (eventManager.participants.isEmpty()) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7The &cSpleef &7event ended in a draw!"));
            Bukkit.broadcastMessage("");
            cancelSpleefEvent();
        } else if (eventManager.participants.size() == 1) {
            Player winner = eventManager.participants.iterator().next();
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d" + winner.getName() + " &bhas won the Spleef event!"));
            Bukkit.broadcastMessage("");

            winner.setHealth(20.0);
            winner.setFoodLevel(20);
            winner.setSaturation(20.0f);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + winner.getName() + " 2000");
            winner.setGlowing(false);

            World world = Bukkit.getWorld("world");
            if (world != null) {
                Location customSpawnLocation = new Location(world, 773.472, 61.0, -80.483);

                User user = Main.getEssentials().getUser(winner);
                if (user != null) {
                    user.setLastLocation(customSpawnLocation);
                    Bukkit.getLogger().info("Set /back for winner " + winner.getName() + " to custom spawn location.");
                }
            }
            cancelSpleefEvent();
        }
    }

    private void Timer() {

        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
            timerTask = null;
        }

        final int[] timeLeft = {5};

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!eventManager.isSpleefEventActive()) {
                    this.cancel();
                    return;
                }

                timeLeft[0]--;
                for (Player participant : eventManager.getParticipants()) {
                    participant.playSound(participant, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1f);
                    participant.sendTitle(ChatColor.translateAlternateColorCodes('&', "&6&l" + timeLeft[0] + " MINUTES LEFT"),
                            ChatColor.translateAlternateColorCodes('&', "&7until the game ends"),
                            10, 20, 10);
                }

                if (timeLeft[0] <= 0) {
                    cancelSpleefEvent();
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 1200L, 1200L);
    }
}
