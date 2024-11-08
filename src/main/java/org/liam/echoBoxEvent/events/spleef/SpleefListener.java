package org.liam.echoBoxEvent.events.spleef;

import com.earth2me.essentials.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.liam.echoBoxEvent.EventManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.items.InventoryHolderManager;
import org.liam.echoBoxEvent.Main;
import org.liam.echoBoxEvent.maps.SpleefMapReset;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SpleefListener implements Listener {

    private final EventManager eventManager;
    private final SpleefManager spleefManager;
    private final SpawnsManager spawnsManager;

    public SpleefListener(EventManager eventManager, SpleefManager spleefManager, SpawnsManager spawnsManager) {
        this.eventManager = eventManager;
        this.spleefManager = spleefManager;
        this.spawnsManager = spawnsManager;
    }

    private boolean inSpleef(Player player) {
        return !eventManager.isSpleefEventActive() || !eventManager.isPlayerInEvent(player);
    }

    @EventHandler
    public void onItemDamageChange(PlayerItemDamageEvent event) {
        ItemStack held = event.getItem();

        if (held.getType() == Material.DIAMOND_SHOVEL
                && held.getItemMeta() != null
                && ChatColor.stripColor(held.getItemMeta().getDisplayName()).equalsIgnoreCase("SPLEEF SHOVEL")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (eventManager.isPlayerInEvent(player) && eventManager.isSpleefEventActive()) {
            eventManager.participants.remove(player);
            InventoryHolderManager.restorePlayerInventory(player);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&d" + player.getName() + " &eleft the spleef event."));

            spleefManager.checkWinnerSpleef();
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (eventManager.isPlayerInEvent(player) && eventManager.isSpleefEventActive()) {
            if (!player.isOp()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't use commands at the moment."));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (inSpleef(player)) return;

        player.getInventory().clear();
        eventManager.getParticipants().remove(player);

        for (Player participant : eventManager.getParticipants()) {
            participant.playSound(participant.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1f);
        }

        InventoryHolderManager.restorePlayerInventory(player);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7has been eliminated. " + "&7(&c" + eventManager.participants.size() + "&7/&c" + eventManager.Max_Players_For_Spleef + "&7)"));

        World world = Bukkit.getWorld("world");
        if (world != null) {
            Location customSpawnLocation = new Location(world, 773.472, 61.0, -80.483);
            User user = Main.getEssentials().getUser(player);
            if (user != null) {
                user.setLastLocation(customSpawnLocation);
                Bukkit.getLogger().info("Set /back for " + user.getName() + " to custom spawn location.");
            }
        }

        Bukkit.getScheduler().runTaskLater(Main.getPluginInstance(), () -> player.spigot().respawn(), 1L);

        spleefManager.checkWinnerSpleef();
        player.setGlowing(false);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (eventManager.isPlayerInEvent(player) && eventManager.isSpleefEventActive()) {
            if (InventoryHolderManager.playerInventories.containsKey(player)) {
                InventoryHolderManager.restorePlayerInventory(player);
            }

            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (inSpleef(player))
            return;

        if (spleefManager.frozenPlayers.contains(player)) {
            if (event.getFrom().getX() != Objects.requireNonNull(event.getTo()).getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        }


        if (Math.abs(event.getFrom().getY() - Objects.requireNonNull(event.getTo()).getY()) < 0.1) {
            return;
        }

        if (eventManager.getGameState() == GameState.WAITING && player.getLocation().getY() < 25) {
            Location spleefWaitingSpawn = spawnsManager.getSpawn("spleef", eventManager.getCurrentMap(), "waiting");
            if (spleefWaitingSpawn == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError."));
                return;
            }
            player.teleport(spleefWaitingSpawn);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player victim) {
            if (eventManager.getParticipants().contains(damager) && eventManager.getParticipants().contains(victim)) {
                if (spleefManager.frozenPlayers.contains(damager) || spleefManager.frozenPlayers.contains(victim)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (eventManager.getParticipants().contains(player) && eventManager.isSpleefEventActive()) {
            if (spleefManager.frozenPlayers.contains(player)) {
                event.setCancelled(true);
                return;
            }

            if (block.getType() == Material.SNOW_BLOCK) {
                event.setDropItems(false);

                double chance = ThreadLocalRandom.current().nextDouble();

                if (chance <= 0.01) {
                    ItemStack tntSnowball = new ItemStack(Material.SNOWBALL, 1);
                    ItemMeta tntSnowballMeta = tntSnowball.getItemMeta();

                    assert tntSnowballMeta != null;
                    tntSnowballMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lSNOWBALL TNT"));
                    tntSnowballMeta.setLore(List.of("", ChatColor.translateAlternateColorCodes('&', "&cShoot this to make a huge explosion!"), ""));

                    tntSnowballMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    tntSnowballMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    tntSnowball.setItemMeta(tntSnowballMeta);

                    player.getInventory().setItem(1, tntSnowball);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6You got an &eexplosive snowball!"));
                }

                int snowballCount = ThreadLocalRandom.current().nextInt(3, 5);
                ItemStack snowballs = new ItemStack(Material.SNOWBALL, snowballCount);

                player.getInventory().addItem(snowballs);

                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball snowball) {
            if (snowball.getShooter() instanceof Player shooter) {
                ItemStack itemInHand = shooter.getInventory().getItemInMainHand();

                if (itemInHand.getType() == Material.SNOWBALL && itemInHand.hasItemMeta()) {
                    ItemMeta meta = itemInHand.getItemMeta();
                    if (meta != null && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&c&lSNOWBALL TNT"))) {
                        snowball.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&lSNOWBALL TNT"));
                        snowball.setCustomNameVisible(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(inSpleef(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball snowball) {

            if (snowball.getShooter() instanceof Player shooter) {

                if (inSpleef(shooter))
                    return;

                if (snowball.getCustomName() != null && snowball.getCustomName().equals(ChatColor.translateAlternateColorCodes('&', "&c&lSNOWBALL TNT"))) {
                    Location hitLocation;

                    if (event.getHitBlock() != null) {
                        hitLocation = event.getHitBlock().getLocation();
                    } else if (event.getHitEntity() != null) {
                        hitLocation = event.getHitEntity().getLocation();
                    } else {
                        return;
                    }


                    explosion(hitLocation);
                    return;
                }

                if (event.getHitBlock() != null) {
                    Block hitBlock = event.getHitBlock();
                    if (hitBlock.getType() == Material.SNOW_BLOCK) {
                        SpleefMapReset.defaultBlocks.putIfAbsent(hitBlock.getLocation(), hitBlock.getType());
                        hitBlock.setType(Material.AIR);
                    }
                }

                if (event.getHitEntity() instanceof Player hitPlayer) {
                    if (!inSpleef(hitPlayer)) {
                        Location hitLocation = hitPlayer.getLocation();
                        Location shooterLocation = shooter.getLocation();

                        Vector knockback = hitLocation.toVector().subtract(shooterLocation.toVector()).normalize();
                        knockback.setY(0.3);

                        hitPlayer.setVelocity(knockback.multiply(0.5));
                        hitPlayer.damage(0.01, shooter);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(inSpleef(player)) return;

        if (Objects.requireNonNull(event.getCurrentItem()).getType() == Material.SNOW_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (!(victim instanceof Player target) || !(damager instanceof Player attacker)) {
            return;
        }

        if (inSpleef(attacker) || inSpleef(target)) {
            return;
        }

        if (eventManager.isPlayerInEvent(attacker) && eventManager.isPlayerInEvent(target) && eventManager.isEventSpleef) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        if (!eventManager.isSpleefEventActive()) return;

        if (event.getLocation().getWorld() == null || !event.getLocation().getWorld().getName().equals("events")) return;

        if (event.getEntity() instanceof TNTPrimed) {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.SNOW_BLOCK) {
                    SpleefMapReset.defaultBlocks.putIfAbsent(block.getLocation(), block.getType());
                    block.setType(Material.AIR);
                }
            }
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityDamageByTNT(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            if (eventManager.isSpleefEventActive() && event.getEntity().getWorld().getName().equals("events")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryChange(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(inSpleef(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (inSpleef(player)) return;

        event.setCancelled(true);
    }

    private void explosion(Location center) {
        int[][] directions = {
                {0, 0},
                {2, 0},
                {0, 2},
                {-2, 0},
                {0, -2}
        };

        for (int[] direction : directions) {
            int x = direction[0];
            int z = direction[1];

            Location blockLocation = center.clone().add(x, 0, z);
            Block block = blockLocation.getBlock();

            if (block.getType() == Material.SNOW_BLOCK) {
                SpleefMapReset.defaultBlocks.putIfAbsent(block.getLocation(), block.getType());
                block.setType(Material.AIR);
            }

            Objects.requireNonNull(center.getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, center, 1);
            center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        }
    }

    @EventHandler
    public void PlayerDamagedInWaitingState(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {

            if(inSpleef(player)) return;

            if (eventManager.getGameState() == GameState.WAITING) {
                event.setCancelled(true);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            }
        }
    }
}


