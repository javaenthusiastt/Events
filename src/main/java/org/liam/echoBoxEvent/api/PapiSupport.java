package org.liam.echoBoxEvent.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.liam.echoBoxEvent.players.Data.PlayerData;

import java.util.UUID;

public class PapiSupport extends PlaceholderExpansion {

    private final PlayerData playerData;

    public PapiSupport(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public String getIdentifier() {
        return "event";
    }

    @Override
    public String getAuthor() {
        return "sorryplspls";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || player.getUniqueId() == null) return "";

        UUID uuid = player.getUniqueId();

        switch (params.toLowerCase()) {
            case "points":
                return String.valueOf(playerData.getPoints(uuid));
            case "wins":
                return String.valueOf(playerData.getWins(uuid));
            case "losses":
                return String.valueOf(playerData.getLosses(uuid));
            case "total_games":
                return String.valueOf(playerData.getTotalGamesPlayed(uuid));
            case "spleef_played":
                return String.valueOf(playerData.getSpleefMatchesPlayed(uuid));
            case "sumo_played":
                return String.valueOf(playerData.getSumoMatchesPlayed(uuid));
            case "murdermystery_played":
                return String.valueOf(playerData.getMurderMysteryMatchesPlayed(uuid));
            case "sumo_hits":
                return String.valueOf(playerData.getSumoHits(uuid));
            case "sumo_wins":
                return String.valueOf(playerData.getSumoWins(uuid));
            case "sumo_losses":
                return String.valueOf(playerData.getSumoLosses(uuid));
            case "murdermystery_wins":
                return String.valueOf(playerData.getMurderMysteryWins(uuid));
            case "murdermystery_losses":
                return String.valueOf(playerData.getMurderMysteryLosses(uuid));
            case "spleef_wins":
                return String.valueOf(playerData.getSpleefWins(uuid));
            case "spleef_losses":
                return String.valueOf(playerData.getSpleefLosses(uuid));
            case "spleef_snow_mined":
                return String.valueOf(playerData.getSpleefSnowMined(uuid));

            case "owned_winning_cosmetics":
                return String.valueOf(playerData.getOwnedCosmeticsInCategory(uuid, "winning_cosmetics"));
            case "owned_death_cosmetics":
                return String.valueOf(playerData.getOwnedCosmeticsInCategory(uuid, "death_cosmetics"));
            case "owned_lobby_particles":
                return String.valueOf(playerData.getOwnedCosmeticsInCategory(uuid, "lobby_particles"));

            case "equipped_winning":
                return playerData.getEquippedCosmetic(uuid, "winning");
            case "equipped_death":
                return playerData.getEquippedCosmetic(uuid, "death");
            case "equipped_lobby_particle":
                return playerData.getEquippedCosmetic(uuid, "lobby_particle");
        }
        return null;
    }
}
