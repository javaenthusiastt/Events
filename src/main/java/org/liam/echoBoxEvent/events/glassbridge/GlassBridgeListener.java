package org.liam.echoBoxEvent.events.glassbridge;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.maps.GlassBridgeMapReset;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.*;

public class GlassBridgeListener implements Listener {


    private final Set<Player> glassbridgefrozen = new HashSet<>();
    private final Random random = new Random();
    private boolean isRunningAlready = false;
    private boolean guaranteeGreen = false;
    private Location activePlatform;


    private final EventManager eventManager;
    private final GlassBridgeManager glassBridgeManager;
    private final SpawnsManager spawnsManager;

    public GlassBridgeListener(EventManager eventManager, GlassBridgeManager glassBridgeManager, SpawnsManager spawnsManager) {
        this.eventManager = eventManager;
        this.glassBridgeManager = glassBridgeManager;
        this.spawnsManager = spawnsManager;
    }

    private boolean inGlassBridge(Player player) {
        return !eventManager.isGlassBridgeEventActive() || !eventManager.isPlayerInEvent(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (damager instanceof Player attacker && damaged instanceof Player victim) {
            if (inGlassBridge(attacker) || inGlassBridge(victim)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (inGlassBridge(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (inGlassBridge(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (inGlassBridge(player)) return;
        String command = event.getMessage().toLowerCase();

        if (!command.equals("/leaveevent")) {
            if (!(player.isOp())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't use commands at the moment except /leaveevent."));
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (inGlassBridge(player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryChange(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (inGlassBridge(player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (block.getType() == Material.GLASS && !isRunningAlready && eventManager.isPlayerInEvent(player) && eventManager.isGlassBridgeEventActive() && eventManager.getGameState() == GameState.IN_PROGRESS) {
            isRunningAlready = true;

            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&2&lRIGHT &7&LOR &C&LWRONG&7&l?"), "", 20, 10, 20);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
            glassbridgefrozen.add(player);

            List<Block> glassBlocksToProcess = new ArrayList<>();
            int centerX = player.getLocation().getBlockX();
            int centerY = player.getLocation().getBlockY();
            int centerZ = player.getLocation().getBlockZ();

            for (int x = centerX - 1; x <= centerX + 1; x++) {
                for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                    for (int y = centerY - 1; y <= centerY + 1; y++) {
                        Block currentBlock = block.getWorld().getBlockAt(x, y, z);
                        if (currentBlock.getType() == Material.GLASS) {
                            glassBlocksToProcess.add(currentBlock);
                        }
                    }
                }
            }

            activePlatform = new Location(player.getWorld(), centerX, centerY, centerZ);

            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean shouldBeGreen;

                    if (guaranteeGreen) {
                        shouldBeGreen = true;
                        guaranteeGreen = false;
                    } else {
                        shouldBeGreen = random.nextBoolean();
                    }

                    if (shouldBeGreen) {
                        for (Block glassBlock : glassBlocksToProcess) {
                            GlassBridgeMapReset.greenblocks.put(glassBlock.getLocation(), glassBlock.getType());
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&A&LCORRECT!"), "", 10, 20, 10);
                            glassBlock.setType(Material.GREEN_STAINED_GLASS);
                        }
                    } else {
                        for (Block glassBlock : glassBlocksToProcess) {
                            guaranteeGreen = true;
                            GlassBridgeMapReset.redblocks.put(glassBlock.getLocation(), glassBlock.getType());
                            glassBlock.setType(Material.AIR);
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c&lWRONG!"), "", 10, 20, 10);
                        }
                    }

                    glassbridgefrozen.remove(player);
                    isRunningAlready = false;
                    activePlatform = null;
                }
            }.runTaskLater(Main.getPluginInstance(), 60);
        }

        if (block.getType() == Material.GLASS && activePlatform != null) {
            Location playerLocation = player.getLocation();
            if (playerLocation.getBlockX() == activePlatform.getBlockX() && playerLocation.getBlockZ() == activePlatform.getBlockZ()) {
                if (!glassbridgefrozen.contains(player)) {
                    glassbridgefrozen.add(player);
                }
            }
        }

        if (glassbridgefrozen.contains(player) && block.getType() != Material.GLASS) {
            glassbridgefrozen.remove(player);
        }

    }


    @EventHandler
    public void onButtonClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (inGlassBridge(player)) return;

        Action click = event.getAction();
        if (click == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.POLISHED_BLACKSTONE_BUTTON) {
                List<Player> closestPlayers = getClosestPlayers(player);

                Player secondPlace = null;
                Player thirdPlace = null;

                if (closestPlayers.size() > 1) {
                    secondPlace = closestPlayers.get(1);
                }

                if (closestPlayers.size() > 2) {
                    thirdPlace = closestPlayers.get(2);
                }

                glassBridgeManager.endGlassBridgeEvent(player, secondPlace, thirdPlace);
            }
        }
    }


    @EventHandler
    public void restrictMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (glassbridgefrozen.contains(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                player.teleport(new Location(from.getWorld(), from.getX(), to.getY(), from.getZ(), from.getYaw(), from.getPitch()));
            }
        }
    }

    private List<Player> getClosestPlayers(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(player.getServer().getOnlinePlayers());
        onlinePlayers.remove(player);

        onlinePlayers.sort(Comparator.comparingDouble(p -> p.getLocation().distance(player.getLocation())));

        return onlinePlayers;
    }
}