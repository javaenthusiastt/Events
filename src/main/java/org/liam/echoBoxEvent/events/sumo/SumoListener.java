package org.liam.echoBoxEvent.events.sumo;

import com.earth2me.essentials.User;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.players.Data.PlayerData;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SumoListener implements Listener {

    private final EventManager eventManager;
    private final SpawnsManager spawnsManager;
    private final SumoManager sumoManager;
    private final Main plugin;

    public List<Player> eliminatedPlayers = new ArrayList<>();

    private final PlayerData playerData;

    public SumoListener(EventManager eventManager, SpawnsManager spawnsManager, Main plugin, SumoManager sumoManager, PlayerData playerData) {
        this.eventManager = eventManager;
        this.spawnsManager = spawnsManager;
        this.plugin = plugin;
        this.sumoManager = sumoManager;
        this.playerData = playerData;
    }

    private boolean InSumo(Player player) {
        return !eventManager.isSumoEventActive() || !eventManager.isPlayerInEvent(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        if (sumoManager.getFrozenPlayers().contains(player)) {
            event.setTo(event.getFrom());
            return;
        }

        if (eventManager.getGameState() == GameState.IN_PROGRESS && sumoManager.getBattlingPlayers().contains(player)) {
            if (player.getLocation().getBlock().isLiquid()) {
                player.setHealth(0.0);
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 1L);
                return;
            }
        }

        if (Math.abs(event.getFrom().getY() - Objects.requireNonNull(event.getTo()).getY()) < 0.1) {
            return;
        }

        if (eventManager.getGameState() == GameState.WAITING && player.getLocation().getY() < 25) {
            Location sumoWaitingSpawn = spawnsManager.getSpawn("sumo", eventManager.getCurrentMap(), "waiting");
            if (sumoWaitingSpawn == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError."));
                return;
            }
            player.teleport(sumoWaitingSpawn);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryChange(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (InSumo(player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        if (eventManager.getGameState() == GameState.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player attacker && damaged instanceof Player victim) {
            if (InSumo(attacker) || InSumo(victim))
                return;
            if (eventManager.getGameState() == GameState.IN_PROGRESS) {
                if (sumoManager.getBattlingPlayers().contains(attacker) && sumoManager.getBattlingPlayers().contains(victim)) {
                    playerData.addSumoHits(victim.getUniqueId());
                    playerData.addSumoHits(attacker.getUniqueId());
                    if(attacker.getLocation().getY() > 10 && victim.getLocation().getY() > 10){
                        attacker.setHealth(20.0);
                        victim.setHealth(20.0);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if (eventManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(InSumo(player)) return;
        String command = event.getMessage().toLowerCase();

        if (!command.equals("/leaveevent")) {
            if (!(player.isOp())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't use commands at the moment except /leaveevent."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        player.getInventory().clear();
        eventManager.participants.remove(player);

        InventoryHolderManager.restorePlayerInventory(player);

        if (eventManager.getGameState() == GameState.WAITING) {
            if (eventManager.getParticipants().size() < eventManager.Min_Players_For_Sumo) {
                sumoManager.cancelSumoEvent();
            }
        } else if (eventManager.getGameState() == GameState.IN_PROGRESS) {
            if (sumoManager.getBattlingPlayers().contains(player)) {
                sumoManager.handlePlayerLeaveDuringFight(player);
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (InSumo(player)) return;

        player.getInventory().clear();
        eventManager.participants.remove(player);

        InventoryHolderManager.restorePlayerInventory(player);

        World world = Bukkit.getWorld("world");
        if (world != null) {
            Location customSpawnLocation = new Location(world, 773.472, 61.0, -80.483);
            User user = Main.getEssentials().getUser(player);
            if (user != null) {
                user.setLastLocation(customSpawnLocation);
            }
        }

        playerData.addLosses(player.getUniqueId());
        playerData.addSumoLosses(player.getUniqueId());

        if (sumoManager.getBattlingPlayers().contains(player)) {
            Player remainingPlayer = sumoManager.getBattlingPlayers().get(0).equals(player)
                    ? sumoManager.getBattlingPlayers().get(1)
                    : sumoManager.getBattlingPlayers().get(0);

            sumoManager.getBattlingPlayers().clear();

            for (Player eventPlayers : eventManager.getParticipants()) {
                eventPlayers.playSound(eventPlayers.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
                eventPlayers.setFoodLevel(20);
                eventPlayers.setSaturation(20f);
                eventPlayers.setHealth(20);
            }

            Location sumoArena = spawnsManager.getSpawn("sumo", eventManager.getCurrentMap(), "arena");

            if (sumoArena != null) {
                remainingPlayer.teleport(sumoArena);
            }

            remainingPlayer.playEffect(EntityEffect.TOTEM_RESURRECT);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7has been eliminated by &c" + remainingPlayer.getName() + " &7(&c" + eventManager.participants.size() + "&7/&c" + eventManager.Max_Players_For_Sumo + "&7)"));

            remainingPlayer.chat("GG!");
            player.chat("GG!");

            eliminatedPlayers.add(player);

            Bukkit.getScheduler().runTaskLater(Main.getPluginInstance(), () -> {
                remainingPlayer.setFoodLevel(20);
                remainingPlayer.setSaturation(20f);

                int remainingPlayersCount = eventManager.getParticipants().size();

                if (remainingPlayersCount > 1) {
                    sumoManager.RandomFight();
                } else {
                    Player winner = eventManager.getParticipants().iterator().next();
                    sumoManager.endSumoEvent(winner, eliminatedPlayers.get(0), eliminatedPlayers.get(1));
                    playerData.addSumoWins(winner.getUniqueId());
                    playerData.addPoints(winner.getUniqueId());
                    playerData.addWins(winner.getUniqueId());
                }
            }, 40L);

        } else {
            if (eventManager.getParticipants().size() == 1) {
                Player winner = eventManager.getParticipants().iterator().next();
                sumoManager.endSumoEvent(winner, eliminatedPlayers.get(0), eliminatedPlayers.get(1));
                playerData.addSumoWins(winner.getUniqueId());
                playerData.addPoints(winner.getUniqueId());
                playerData.addWins(winner.getUniqueId());
            } else if (eventManager.getParticipants().size() >= 2) {
                sumoManager.RandomFight();
            } else {
                sumoManager.cancelSumoEvent();
            }
        }
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (InSumo(player))
            return;

        if (InventoryHolderManager.playerInventories.containsKey(player)) {
            InventoryHolderManager.restorePlayerInventory(player);
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }
}
