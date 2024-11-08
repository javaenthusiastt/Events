package org.liam.echoBoxEvent.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.liam.echoBoxEvent.players.Data.PlayerData;

import java.util.UUID;
import java.util.Arrays;

public class EventInformation implements CommandExecutor, Listener {

    private final PlayerData playerData;

    private final String title = ChatColor.translateAlternateColorCodes('&', "&8Your Event Stats");

    public EventInformation(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Loading your stats..."));

        UUID playerUUID = player.getUniqueId();

        playerData.getData(playerUUID);

        int points = playerData.getPoints(playerUUID);
        int wins = playerData.getWins(playerUUID);
        int losses = playerData.getLosses(playerUUID);
        int spleefMatches = playerData.getSpleefMatchesPlayed(playerUUID);
        int sumoMatches = playerData.getSumoMatchesPlayed(playerUUID);
        int murderMysteryMatches = playerData.getMurderMysteryMatchesPlayed(playerUUID);

        int sumoHits = playerData.getSumoHits(playerUUID);
        int sumoWins = playerData.getSumoWins(playerUUID);
        int sumoLosses = playerData.getSumoLosses(playerUUID);

        int spleefWins = playerData.getSpleefWins(playerUUID);
        int spleefLosses = playerData.getSpleefLosses(playerUUID);
        int spleefSnowMined = playerData.getSpleefSnowMined(playerUUID);

        int murderMysteryWins = playerData.getMurderMysteryWins(playerUUID);
        int murderMysteryLosses = playerData.getMurderMysteryLosses(playerUUID);

        int totalGamesPlayed = playerData.getTotalGamesPlayed(playerUUID);

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', title));

        gui.setItem(22, createGuiItem(Material.DIAMOND, "&aTotal Points", "", "&7Total Event Points: &a"+points, "", "&7Click to convert to beacons!"));
        gui.setItem(23, createGuiItem(Material.EMERALD_BLOCK, "&aTotal Wins", "", "&7Total Event Wins: &a"+wins));
        gui.setItem(21, createGuiItem(Material.SKELETON_SKULL, "&aTotal Losses", "", "&7Total Event Losses: &a"+losses));
        gui.setItem(30, createGuiItem(Material.SNOWBALL, "&aYour Spleef Stats", "", "&7Wins: &a"+spleefWins, "&7Losses: &a"+spleefLosses, "&7Snow Mined: &a"+spleefSnowMined, "&7Games: &a"+spleefMatches));
        gui.setItem(31, createGuiItem(Material.STICK, "&aYour Sumo Stats", "", "&7Wins: &a"+sumoWins, "&7Losses: &a"+sumoLosses, "&7Hits: &a"+sumoHits, "&7Games: &a"+sumoMatches));
        gui.setItem(32, createGuiItem(Material.BOW, "&aYour Murder Mystery Stats", "", "&7Wins: &a"+murderMysteryWins, "&7Losses: &a"+murderMysteryLosses, "&7Games: &a"+murderMysteryMatches));
        gui.setItem(49, createGuiItem(Material.BARRIER, "&cClose"));
        player.openInventory(gui);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.getInventory();
        if (!event.getView().getTitle().equals(title)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        if(clickedItem.getType().equals(Material.BARRIER)){
            player.closeInventory();
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.6f);
        }
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Arrays.asList(Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toArray(String[]::new)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
