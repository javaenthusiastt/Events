package org.liam.echoBoxEvent.players.Data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final File playerDataFolder;

    public PlayerData(File data) {
        this.playerDataFolder = new File(data, "playerdata");
        if (!this.playerDataFolder.exists()) {
            this.playerDataFolder.mkdirs();
        }
    }

    public void incrementStat(UUID uuid, String stat) {
        FileConfiguration data = getData(uuid);
        int currentValue = data.getInt(stat, 0);
        data.set(stat, currentValue + 1);
        saveData(uuid, data);
    }

    public FileConfiguration getData(UUID uuid) {
        File playerFile = new File(playerDataFolder, uuid.toString() + ".yml");

        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();

                FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);

                playerData.set("points", 0);
                playerData.set("wins", 0);
                playerData.set("losses", 0);

                playerData.set("sumo_played", 0);
                playerData.set("spleef_played", 0);
                playerData.set("murdermystery_played", 0);

                playerData.set("sumo_hits", 0);
                playerData.set("sumo_wins", 0);
                playerData.set("sumo_losses", 0);

                playerData.set("murdermystery_wins", 0);
                playerData.set("murdermystery_losses", 0);

                playerData.set("spleef_wins", 0);
                playerData.set("spleef_losses", 0);
                playerData.set("spleef_snow_mined", 0);

                playerData.set("glassbridge_played", 0);
                playerData.set("glassbridge_wins", 0);
                playerData.set("glassbridge_losses", 0);

                playerData.set("owned_cosmetics.winning_cosmetics", new ArrayList<String>());
                playerData.set("owned_cosmetics.death_cosmetics", new ArrayList<String>());
                playerData.set("owned_cosmetics.lobby_particles", new ArrayList<String>());

                playerData.set("equipped_cosmetics.winning", null);
                playerData.set("equipped_cosmetics.death", null);
                playerData.set("equipped_cosmetics.lobby_particle", null);


                playerData.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void saveData(UUID uuid, FileConfiguration data) {
        File playerFile = new File(playerDataFolder, uuid.toString() + ".yml");

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("points", 0);
    }

    public int getWins(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("wins", 0);
    }

    public int getLosses(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("losses", 0);
    }

    public int getSpleefMatchesPlayed(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("spleef_played", 0);
    }

    public int getSumoMatchesPlayed(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("sumo_played", 0);
    }

    public int getMurderMysteryMatchesPlayed(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("murdermystery_played", 0);
    }


    public int getSumoHits(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("sumo_hits", 0);
    }

    public int getSumoWins(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("sumo_wins", 0);
    }

    public int getSumoLosses(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("sumo_losses", 0);
    }

    public int getMurderMysteryWins(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("murdermystery_wins", 0);
    }

    public int getMurderMysteryLosses(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("murdermystery_losses", 0);
    }

    public int getSpleefWins(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("spleef_wins", 0);
    }

    public int getSpleefLosses(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("spleef_losses", 0);
    }

    public int getGlassBridgePlayed(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("glassbridge_played", 0);
    }

    public int getGlassBridgeWins(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("glassbridge_wins", 0);
    }

    public int getGlassBridgeLosses(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("glassbridge_losses", 0);
    }

    public int getTotalGamesPlayed(UUID uuid) {
        FileConfiguration data = getData(uuid);
        int sumoPlayed = data.getInt("sumo_played", 0);
        int spleefPlayed = data.getInt("spleef_played", 0);
        int murderMysteryPlayed = data.getInt("murdermystery_played", 0);

        return sumoPlayed + spleefPlayed + murderMysteryPlayed;
    }

    public int getSpleefSnowMined(UUID uuid) {
        FileConfiguration data = getData(uuid);
        return data.getInt("spleef_snow_mined", 0);
    }

    public void addPoints(UUID uuid) {
        incrementStat(uuid, "points");
    }

    public void addWins(UUID uuid) {
        incrementStat(uuid, "wins");
    }

    public void addLosses(UUID uuid) {
        incrementStat(uuid, "losses");
    }

    public void addSpleefPlayed(UUID uuid) {
        incrementStat(uuid, "spleef_played");
    }

    public void addSumoPlayed(UUID uuid) {
        incrementStat(uuid, "sumo_played");
    }

    public void addMurderMysteryPlayed(UUID uuid) {
        incrementStat(uuid, "murdermystery_played");
    }

    public void addSumoHits(UUID uuid) {
        incrementStat(uuid, "sumo_hits");
    }

    public void addSumoWins(UUID uuid) {
        incrementStat(uuid, "sumo_wins");
    }

    public void addSumoLosses(UUID uuid) {
        incrementStat(uuid, "sumo_losses");
    }

    public void addMurderMysteryWins(UUID uuid) {
        incrementStat(uuid, "murdermystery_wins");
    }

    public void addMurderMysteryLosses(UUID uuid) {
        incrementStat(uuid, "murdermystery_losses");
    }

    public void addSpleefWins(UUID uuid) {
        incrementStat(uuid, "spleef_wins");
    }

    public void addSpleefLosses(UUID uuid) {
        incrementStat(uuid, "spleef_losses");
    }

    public void addGlassBridgeGames(UUID uuid){
        incrementStat(uuid, "glassbridge_games");
    }

    public void addGlassBridgeWins(UUID uuid){
        incrementStat(uuid, "glassbridge_wins");
    }

    public void addGlassBridgeLosses(UUID uuid){
        incrementStat(uuid, "glassbridge_losses");
    }

    public List<String> getOwnedCosmeticsInCategory(UUID uuid, String category) {
        FileConfiguration data = getData(uuid);
        return data.getStringList("owned_cosmetics." + category);
    }

    public void addOwnedCosmetic(UUID uuid, String category, String cosmetic) {
        FileConfiguration data = getData(uuid);
        List<String> ownedCosmetics = getOwnedCosmeticsInCategory(uuid, category);

        if (!ownedCosmetics.contains(cosmetic)) {
            ownedCosmetics.add(cosmetic);
            data.set("owned_cosmetics." + category, ownedCosmetics);
            saveData(uuid, data);
        }
    }

    public boolean ownsCosmetic(UUID uuid, String category, String cosmetic) {
        List<String> ownedCosmetics = getOwnedCosmeticsInCategory(uuid, category);
        return ownedCosmetics.contains(cosmetic);
    }

    public String getEquippedCosmetic(UUID uuid, String category) {
        FileConfiguration data = getData(uuid);
        return data.getString("equipped_cosmetics." + category, null);
    }

    public void setEquippedCosmetic(UUID uuid, String category, String cosmetic) {
        FileConfiguration data = getData(uuid);
        data.set("equipped_cosmetics." + category, cosmetic);
        saveData(uuid, data);
    }

    public void clearEquippedCosmetic(UUID uuid, String category) {
        FileConfiguration data = getData(uuid);
        data.set("equipped_cosmetics." + category, null);
        saveData(uuid, data);
    }
}
