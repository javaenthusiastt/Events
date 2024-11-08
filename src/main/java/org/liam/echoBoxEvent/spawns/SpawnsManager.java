package org.liam.echoBoxEvent.spawns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.liam.echoBoxEvent.Main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SpawnsManager {

    private final Main plugin;

    public SpawnsManager(Main plugin) {
        this.plugin = plugin;
    }

    public void createMap(String event, String mapName) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        if (!spawnsConfig.contains(event + "." + mapName)) {
            spawnsConfig.createSection(event + "." + mapName);
            plugin.saveSpawnsConfig();
        }
    }

    public boolean isMapCreated(String event, String mapName) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        return spawnsConfig.contains(event + "." + mapName);
    }

    public void setSpawn(String event, String mapName, String spawnType, Location location) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".world", location.getWorld().getName());
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".x", location.getX());
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".y", location.getY());
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".z", location.getZ());
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".yaw", location.getYaw());
        spawnsConfig.set(event + "." + mapName + "." + spawnType + ".pitch", location.getPitch());
        plugin.saveSpawnsConfig();
    }

    public boolean isSpawnSet(String event, String mapName, String spawnType) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        return spawnsConfig.contains(event + "." + mapName + "." + spawnType + ".world");
    }


    public Location getSpawn(String event, String mapName, String spawnType) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        String path = event + "." + mapName + "." + spawnType;

        if (!spawnsConfig.contains(path + ".world")) {
            Bukkit.getLogger().warning("[EchoBoxEvent] Spawn location not set for: " + path);
            return null;
        }

        World world = Bukkit.getWorld(Objects.requireNonNull(spawnsConfig.getString(path + ".world")));
        if (world == null) {
            Bukkit.getLogger().warning("[EchoBoxEvent] World " + spawnsConfig.getString(path + ".world") + " is not loaded or does not exist.");
            return null;
        }

        return new Location(
                world,
                spawnsConfig.getDouble(path + ".x"),
                spawnsConfig.getDouble(path + ".y"),
                spawnsConfig.getDouble(path + ".z"),
                (float) spawnsConfig.getDouble(path + ".yaw"),
                (float) spawnsConfig.getDouble(path + ".pitch")
        );
    }


    public List<String> getAvailableMaps(String eventType) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();

        if (!spawnsConfig.contains(eventType)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(Objects.requireNonNull(spawnsConfig.getConfigurationSection(eventType)).getKeys(false));
    }

    public List<String> mapKeysGetter(String event, String mapName) {
        FileConfiguration spawnsConfig = plugin.getSpawnsConfig();
        String path = event + "." + mapName;

        if (!spawnsConfig.contains(path)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Objects.requireNonNull(spawnsConfig.getConfigurationSection(path)).getKeys(false));
    }
}

