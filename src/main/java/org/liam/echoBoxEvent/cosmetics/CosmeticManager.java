package org.liam.echoBoxEvent.cosmetics;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.liam.echoBoxEvent.Main;
import org.bukkit.Particle.DustOptions;

import java.util.Objects;
import java.util.Optional;

public class CosmeticManager {

    public static void ParrotCosmetic(Player player) {
        Location location = player.getLocation();

        Parrot parrot = player.getWorld().spawn(location.add(0, 2, 0), Parrot.class);
        parrot.addPassenger(player);
        parrot.setInvulnerable(true);
        parrot.setGliding(true);

        parrot.setAdult();
        parrot.setGlowing(true);

        Color[] colors = {
                Color.fromRGB(255, 0, 0),
                Color.fromRGB(0, 255, 0),
                Color.fromRGB(0, 0, 255),
                Color.fromRGB(255, 255, 0),
                Color.fromRGB(255, 0, 255)
        };

        new BukkitRunnable() {
            int colorIndex = 0;

            @Override
            public void run() {
                if (parrot.isValid() && player.isOnline()) {
                    Location parrotLocation = parrot.getLocation();
                    parrotLocation.setY(player.getLocation().getY() + 1.5);

                    DustOptions dustOptions = new DustOptions(colors[colorIndex], 2.0F);
                    player.getWorld().spawnParticle(Particle.REDSTONE, parrotLocation.clone().subtract(0, 1, 0), 20, dustOptions);

                    colorIndex = (colorIndex + 1) % colors.length;
                } else {
                    if (parrot.isValid()) {
                        parrot.removePassenger(player);
                        parrot.remove();
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (parrot.isValid()) {
                    parrot.removePassenger(player);
                    parrot.remove();
                }
            }
        }.runTaskLater(Main.getPluginInstance(), 190L);
    }

    public static void PumpkinCosmetic(Player player) {
        Location location = player.getLocation();

        Parrot parrot = player.getWorld().spawn(location.add(0, 2, 0), Parrot.class);
        parrot.setInvisible(true);
        parrot.addPassenger(player);

        ArmorStand pumpkinStand = player.getWorld().spawn(location.add(0, 0, 0), ArmorStand.class);
        pumpkinStand.setVisible(false);
        pumpkinStand.setGravity(false);
        pumpkinStand.setInvulnerable(true);
        pumpkinStand.setCustomNameVisible(false);
        Objects.requireNonNull(pumpkinStand.getEquipment()).setHelmet(new org.bukkit.inventory.ItemStack(Material.JACK_O_LANTERN));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (parrot.isValid() && player.isOnline()) {
                    player.getWorld().spawnParticle(Particle.HEART, pumpkinStand.getLocation(), 20);
                    Location parrotLocation = parrot.getLocation();
                    pumpkinStand.teleport(parrotLocation.clone().add(0, -1.55, 0));
                } else {
                    if (parrot.isValid()) {
                        parrot.removePassenger(player);
                        parrot.remove();
                    }
                    if (pumpkinStand.isValid()) {
                        pumpkinStand.remove();
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (parrot.isValid()) {
                    parrot.removePassenger(player);
                    parrot.remove();
                }
                if (pumpkinStand.isValid()) {
                    pumpkinStand.remove();
                }
            }
        }.runTaskLater(Main.getPluginInstance(), 190L);
    }

    public static void FireworksCosmetic(Player player) {
        BukkitRunnable fireworkTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Location loc = player.getLocation();
                    player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 20, 0.5, 1, 0.5, 0.1);
                } else {
                    this.cancel();
                }
            }
        };


        fireworkTask.runTaskTimer(Main.getPluginInstance(), 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                fireworkTask.cancel();
            }
        }.runTaskLater(Main.getPluginInstance(), 200L);
    }

    public static void AuraCosmetic(Player player) {
        Location loc = player.getLocation();
        int particleCount = 25;
        double particleDistance = 1;
        double[] height = {0.4};
        double heightIncrement = 0.05;
        double pushRadius = 4.0;
        double pushStrength = 0.7;

        BukkitRunnable particleTask = new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < particleCount; i++) {
                    double x = Math.cos(angle + (i * (2 * Math.PI / particleCount))) * particleDistance;
                    double z = Math.sin(angle + (i * (2 * Math.PI / particleCount))) * particleDistance;

                    for (double y = 0; y < height[0]; y += 0.5) {
                        Location particleLocation = loc.clone().add(x, y, z);
                        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, particleLocation, 1, 0, 0, 0, 0);
                    }
                }

                height[0] += heightIncrement;
                angle += 0.1;

                for (Player nearby : player.getWorld().getPlayers()) {
                    if (!nearby.equals(player) && nearby.getLocation().distance(loc) <= pushRadius) {
                        Vector pushDirection = nearby.getLocation().toVector().subtract(loc.toVector()).normalize();
                        pushDirection.multiply(pushStrength);
                        nearby.setVelocity(pushDirection);
                    }
                }
            }
        };

        particleTask.runTaskTimer(Main.getPluginInstance(), 0L, 2L);

        new BukkitRunnable() {
            @Override
            public void run() {
                particleTask.cancel();
            }
        }.runTaskLater(Main.getPluginInstance(), 200L);
    }

    public static void ShadowCloakCosmetic(Player player) {
        Location loc = player.getLocation();
        int particleCount = 150;
        double particleDistance = 0.5;
        double yOffsetMax = 2.5;
        boolean[] invisible = {false};

        BukkitRunnable particleTask = new BukkitRunnable() {
            int ticksPassed = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (!invisible[0]) {
                    for (int i = 0; i < particleCount; i++) {
                        double xOffset = (Math.random() - 0.5) * particleDistance * 2;
                        double yOffset = Math.random() * yOffsetMax;
                        double zOffset = (Math.random() - 0.5) * particleDistance * 2;

                        Location particleLocation = loc.clone().add(xOffset, yOffset, zOffset);
                        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, particleLocation, 1, 0, 0, 0, 0);
                    }
                }

                ticksPassed += 2;
                if (ticksPassed >= 60 && !invisible[0]) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
                    invisible[0] = true;

                    Particle.DustOptions blackDust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 1);

                    BukkitRunnable trailTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline() || !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                this.cancel();
                                return;
                            }

                            Location trailLocation = player.getLocation().clone().subtract(player.getLocation().getDirection().normalize().multiply(0.5));
                            trailLocation.setY(trailLocation.getY() + 0.2);

                            player.getWorld().spawnParticle(Particle.REDSTONE, trailLocation, 1, blackDust);
                        }
                    };

                    trailTask.runTaskTimer(Main.getPluginInstance(), 0L, 2L);

                }

                if (invisible[0]) {
                    this.cancel();
                }
            }
        };

        particleTask.runTaskTimer(Main.getPluginInstance(), 0L, 2L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (invisible[0]) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }.runTaskLater(Main.getPluginInstance(), 200L);
    }

    public static void RainbowEffectCosmetic(Player player) {
        int particleCount = 400;
        double radius = 5.0;

        BukkitRunnable rainbowTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                for (int j = 0; j < particleCount; j++) {
                    double x = (Math.random() - 0.5) * radius;
                    double y = (Math.random() - 0.5) * radius;
                    double z = (Math.random() - 0.5) * radius;

                    Color color = Color.fromRGB((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
                    DustOptions dustOptions = new DustOptions(color, 1.0F);

                    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x, y, z), 1, dustOptions);
                }

                this.cancel();
            }
        };
        rainbowTask.runTask(Main.getPluginInstance());
    }

    public static void SnowstormCosmetic(Player player) {
        int particleCount = 80;
        double radius = 1.0;

        BukkitRunnable snowstormTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                for (int j = 0; j < particleCount; j++) {
                    double x = (Math.random() - 0.5) * radius;
                    double y = Math.random() * 2;
                    double z = (Math.random() - 0.5) * radius;
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(x, y, z), 1);
                }
            }
        };

        new BukkitRunnable() {
            @Override
            public void run() {
                snowstormTask.cancel();
            }
        }.runTaskLater(Main.getPluginInstance(), 200L);

        snowstormTask.runTaskTimer(Main.getPluginInstance(), 0L, 2L);
    }

    public static void MagicShieldCosmetic(Player player) {
        BukkitRunnable shieldTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                double radius = 1.5;
                int particleCount = 100;

                for (int i = 0; i < particleCount; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x, 1.0, z), 1);
                }
            }
        };

        shieldTask.runTaskTimer(Main.getPluginInstance(), 0L, 5L);
    }

    public static void Temporary1(Player shooter) {
        double range = 15.0;

        Optional<Player> closestPlayer = shooter.getWorld().getPlayers().stream()
                .filter(target -> !target.equals(shooter) && target.getLocation().distance(shooter.getLocation()) <= range)
                .min((p1, p2) -> Double.compare(p1.getLocation().distance(shooter.getLocation()), p2.getLocation().distance(shooter.getLocation())));

        if (closestPlayer.isEmpty()) {
            return;
        }

        Player target = closestPlayer.get();

        BukkitRunnable fireballTask = new BukkitRunnable() {
            int fireworkCount = 0;
            final int maxFireworks = 20;

            @Override
            public void run() {
                if (!shooter.isOnline() || !target.isOnline() || fireworkCount >= maxFireworks) {
                    this.cancel();
                    return;
                }

                Location fireworkLocation = shooter.getLocation().add(0, 1, 0);
                Firework firework = shooter.getWorld().spawn(fireworkLocation, Firework.class);

                Vector direction = target.getLocation().toVector().subtract(fireworkLocation.toVector()).normalize();
                firework.setVelocity(direction.multiply(2));

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(Color.RED)
                        .withFade(Color.YELLOW)
                        .with(FireworkEffect.Type.CREEPER)
                        .build();
                firework.getFireworkMeta().addEffect(effect);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (firework.getLocation().distance(target.getLocation()) < 2.0) {
                            DustOptions dustOptions = new DustOptions(Color.RED, 3.5F);
                            target.getWorld().spawnParticle(Particle.REDSTONE, target.getLocation(), 100, dustOptions);

                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getPluginInstance(), 0L, 1L);

                fireworkCount++;
            }
        };

        fireballTask.runTaskTimer(Main.getPluginInstance(), 0L, 5L);
    }

    public static void GalaxyOrbitalCosmetic(Player player) {
        int starCount = 30;
        double orbitRadius = 1.7;
        double[] heightOffsets = {0.4, 1.8, 2.4};

        BukkitRunnable orbitalTask = new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                Location playerLocation = player.getLocation();

                for (int i = 0; i < starCount; i++) {
                    double angleOffset = i * (2 * Math.PI / starCount);
                    double x = Math.cos(angle + angleOffset) * orbitRadius;
                    double z = Math.sin(angle + angleOffset) * orbitRadius;
                    double y = heightOffsets[i % heightOffsets.length];

                    Color starColor = (i % 2 == 0) ? Color.fromRGB(255, 0, 0) : Color.fromRGB(0, 0, 0);
                    DustOptions dustOptions = new DustOptions(starColor, 1.5F);

                    player.getWorld().spawnParticle(Particle.REDSTONE, playerLocation.clone().add(x, y, z), 1, dustOptions);
                }

                if (angle % 40 == 0) {
                    Vector direction = new Vector(Math.random() - 0.5, Math.random() * 0.5, Math.random() - 0.5).normalize();

                    for (int i = 0; i < 10; i++) {
                        Location cometLocation = playerLocation.clone().add(direction.clone().multiply(i * 0.3));

                        Color cometColor = Color.fromRGB(255, 0, 0);
                        DustOptions cometDust = new DustOptions(cometColor, 2.0F);

                        player.getWorld().spawnParticle(Particle.REDSTONE, cometLocation, 1, cometDust);
                    }
                }

                angle += 0.05;
            }
        };

        orbitalTask.runTaskTimer(Main.getPluginInstance(), 0L, 2L);

        new BukkitRunnable() {
            @Override
            public void run() {
                orbitalTask.cancel();
            }
        }.runTaskLater(Main.getPluginInstance(), 200L);
    }

    public static void GlowingCometCosmetic(Player player) {
        boolean[] isGlowing = {false};
        player.setGlowing(true);
        new BukkitRunnable() {
            int ticks = 0;
            final double height = 10.0 + Math.random() * 5;
            double yOffset = 0;
            final int particleCount = 50;
            final Sound explosionSound = Sound.ENTITY_FIREWORK_ROCKET_BLAST;

            @Override
            public void run() {
                if (ticks < 6) {
                    isGlowing[0] = !isGlowing[0];
                    player.setGlowing(isGlowing[0]);
                    ticks++;
                } else {
                    player.setGlowing(false);
                    new BukkitRunnable() {
                        int cometTicks = 0;
                        Location startLocation = player.getLocation().add(0, 1.5, 0);

                        @Override
                        public void run() {
                            if (yOffset < height) {
                                startLocation = player.getLocation().add(0, 1.5, 0);
                                for (int i = 0; i < particleCount; i++) {
                                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, startLocation.clone().add(0, yOffset, 0), 1);
                                }

                                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, startLocation.clone().add(0, -0.5, 0), 1);
                                yOffset += 0.3;
                                cometTicks++;
                            } else {
                                for (int i = 0; i < 100; i++) {
                                    double angle = Math.random() * Math.PI * 2;
                                    double x = Math.cos(angle) * Math.random();
                                    double z = Math.sin(angle) * Math.random();
                                    player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, startLocation.clone().add(0, height, 0).add(x, 0, z), 0);
                                }

                                player.getWorld().playSound(player.getLocation(), explosionSound, 1.0F, 1.0F);

                                for (int i = 0; i < 30; i++) {
                                    player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 1);
                                }

                                for (int i = 0; i < 60; i++) {
                                    double angle = (i * Math.PI * 2) / 60;
                                    double x = Math.cos(angle) * 2.5;
                                    double z = Math.sin(angle) * 2.5;
                                    player.getWorld().spawnParticle(Particle.REDSTONE, startLocation.clone().add(0, height, 0).add(x, 0, z), 1, new Particle.DustOptions(Color.GREEN, 3F));
                                    for (int j = 1; j <= 4; j++) {
                                        player.getWorld().spawnParticle(Particle.REDSTONE, startLocation.clone().add(0, height, 0).add(x, -(2 * j), 0), 1, new Particle.DustOptions(Color.GREEN, 1.5F));
                                        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, startLocation.clone().add(0, height, 0).add(x, -(2 * j + 1), 0), 1, 1, 1, 1, 1);
                                    }
                                }
                                this.cancel();

                                new BukkitRunnable() {
                                    int fallTicks = 0;
                                    final double fallSpeed = 0.4;
                                    final double playerY = player.getLocation().getY();

                                    @Override
                                    public void run() {
                                        Location currentExplosionLocation = player.getLocation().add(0, height, 0);
                                        if (fallTicks * fallSpeed < (currentExplosionLocation.getY() - playerY)) {
                                            for (int i = 0; i < 60; i++) {
                                                double angle = (i * Math.PI * 2) / 60;
                                                double x = Math.cos(angle) * 2.5;
                                                double z = Math.sin(angle) * 2.5;

                                                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, currentExplosionLocation.clone().add(x, -fallTicks * fallSpeed, z), 1);
                                            }
                                            fallTicks++;
                                        } else {
                                            for (int i = 0; i < 30; i++) {
                                                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 1);
                                            }
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(Main.getPluginInstance(), 0L, 2L);
                            }
                        }
                    }.runTaskTimer(Main.getPluginInstance(), 0L, 2L);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 5L);
    }

    public static void DragonBreathStorm(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            final int stormDuration = 80;
            double radius = 2.5;
            final Location startLocation = player.getLocation().add(0, 1.5, 0);

            @Override
            public void run() {
                if (ticks < stormDuration) {
                    Location playerLocation = player.getLocation().add(0, 1.5, 0);

                    double angle = (ticks * Math.PI / 10);
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;

                    player.getWorld().spawnParticle(Particle.FLAME, playerLocation.clone().add(x, ticks * 0.05, z), 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, playerLocation.clone().add(x, ticks * 0.05, z), 0);

                    player.getWorld().spawnParticle(Particle.FLAME, playerLocation.clone().add(x, ticks * 0.1, z), 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, playerLocation.clone().add(x, ticks * 0.1, z), 0);

                    for (int i = 0; i < 3; i++) {
                        double sparkX = (Math.random() - 0.5) * 2;
                        double sparkZ = (Math.random() - 0.5) * 2;
                        player.getWorld().spawnParticle(Particle.CRIT, playerLocation.clone().add(sparkX, ticks * 0.05, sparkZ), 0);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, playerLocation.clone().add(sparkX, ticks * 0.05, sparkZ), 0);
                    }

                    if (ticks % 10 == 0 && radius < 4) {
                        radius += 0.2;
                    }

                    ticks++;
                } else {
                    Location finalLocation = player.getLocation().add(0, 1.5, 0);

                    for (int i = 0; i < 100; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double x = Math.cos(angle) * Math.random() * 2.5;
                        double z = Math.sin(angle) * Math.random() * 2.5;

                        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, finalLocation.clone().add(x, 0, z), 0);
                        player.getWorld().spawnParticle(Particle.FLAME, finalLocation.clone().add(x, 0, z), 0);
                        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, finalLocation.clone().add(x, 0, z), 0);
                    }

                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 1L);
    }

    public static void DetailedHeartShape(Player player) {
        new BukkitRunnable() {
            final int duration = 100;
            int ticks = 0;
            final double detailScale = 0.04;
            final int particleCount = 300;

            @Override
            public void run() {
                if (ticks < duration) {
                    Location playerLocation = player.getLocation().add(0, 4.8, 0);

                    for (int i = 0; i < particleCount; i++) {
                        double t = i * detailScale;
                        double x = 20 * Math.pow(Math.sin(t), 3);
                        double y = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);

                        double randomX = Math.random() * 0.2 - 0.1;
                        double randomY = Math.random() * 0.2 - 0.1;

                        player.getWorld().spawnParticle(Particle.REDSTONE,
                                playerLocation.clone().add((x / 10) + randomX, (y / 10) + randomY, 0),
                                1,
                                new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.6F));

                        if (i % 10 == 0) {
                            player.getWorld().spawnParticle(Particle.REDSTONE,
                                    playerLocation.clone().add((x / 10), (y / 10), 0),
                                    1,
                                    new Particle.DustOptions(Color.fromRGB(255, 128, 128), 3.0F));
                        }
                    }

                    for (int i = 0; i < 2; i++) {
                        player.getWorld().spawnParticle(Particle.HEART,
                                playerLocation.clone().add(0, 2.5, 0),
                                1);
                    }

                    ticks++;
                } else {
                    for (int i = 0; i < 100; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double radius = Math.random() * 2;
                        double x = Math.cos(angle) * radius;
                        double z = Math.sin(angle) * radius;
                        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(x, 0.5, z), 1);
                        player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation().add(x, 2, z), 0);
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 1L);
    }

    public static void RainDropper(Player player) {
        new BukkitRunnable() {
            final int radius = 5;
            int ticks = 0;
            final int duration = 100;

            @Override
            public void run() {
                if (ticks < duration) {
                    Location playerLocation = player.getLocation();

                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {
                            if (x * x + z * z <= radius * radius) {
                                player.getWorld().spawnParticle(Particle.REDSTONE, playerLocation.clone().add(x, 6, z), 1, new Particle.DustOptions(Color.WHITE, 1F));
                            }
                        }
                    }

                    new BukkitRunnable() {
                        int fallTicks = 0;
                        final int maxFallTicks = 20;

                        @Override
                        public void run() {
                            double xOffset = (Math.random() - 0.5) * 10;
                            double zOffset = (Math.random() - 0.5) * 10;

                            Location dropLocation = player.getLocation().add(xOffset, 10, zOffset);

                            if (fallTicks < maxFallTicks) {
                                player.getWorld().spawnParticle(Particle.WATER_DROP, dropLocation.clone().add(0, -fallTicks * 0.5, 0), 0);
                                fallTicks++;
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Main.getPluginInstance(), 0L, 1L);

                    ticks++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPluginInstance(), 0L, 1L);
    }
}

