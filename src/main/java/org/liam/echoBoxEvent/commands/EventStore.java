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
import org.jetbrains.annotations.NotNull;
import org.liam.echoBoxEvent.players.Data.PlayerData;

import java.util.Arrays;
import java.util.UUID;

public class EventStore implements CommandExecutor, Listener {

    private final PlayerData playerData;
    private final String title = ChatColor.translateAlternateColorCodes('&', "&8Event Store");


    public EventStore(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        if(!(player.isOp())){
            player.sendMessage(ChatColor.RED + "Not yet...");
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Loading store..."));

        UUID playerUUID = player.getUniqueId();
        playerData.getData(playerUUID);
        int points = playerData.getPoints(playerUUID);

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', title));

        gui.setItem(52, defaultItems(Material.DIAMOND, "&aYour Store Tokens", "&7"+points));
        gui.setItem(53, defaultItems(Material.BARRIER, "&cClose"));

        gui.setItem(10, cosmeticItem("&cParrot Flyer",
                "&8Event Winning Animation",
                "", "&7Fly on a parrot to celebrate","&7your win!", "", "&7Rarity: &aNORMAL ★", "&7Cost: &22500 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(11, cosmeticItem("&cPumpkin Flyer",
                "&8Event Winning Animation",
                "", "&7Fly on a pumpkin to celebrate","&7your win!", "", "&7Rarity: &aNORMAL ★", "&7Cost: &22500 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(12, cosmeticItem("&cFirework Spreader",
                "&8Event Winning Animation",
                "", "&7Get all covered in cool firework effects","&7to celebrate", "&7your win!", "", "&7Rarity: &aNORMAL ★", "&7Cost: &22500 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(13, cosmeticItem("&cRainbow Bomb",
                "&8Event Join Animation",
                "", "&7When you join a event, a big explosion","&7of random rainbow colors", "&7will appear at you!", "", "&7Rarity: &aNORMAL ★", "&7Cost: &22500 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(14, cosmeticItem("&cAura Pusher",
                "&8Event Join Animation",
                "", "&7When you join a event, a cool magic particle","&7will spawn and push close players", "&7away!", "", "&7Rarity: &5EPIC ★", "&7Cost: &215000 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(15, cosmeticItem("&cShadow Cloaker",
                "&8Event Join Animation",
                "", "&7When you join a event, you will be covered","&7in dark smoke and", "&7disappear for a few seconds!", "", "&7Rarity: &5EPIC ★", "&7Cost: &215000 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(16, cosmeticItem("&cRain Dropper",
                "&8Event Join Animation",
                "", "&7When you join a event, a sky above","&7will spawn", "&7and start raining for a few seconds!", "", "&7Rarity: &5EPIC ★", "&7Cost: &215000 &8◄", "", "&aClick to buy this cosmetic!"));


        gui.setItem(19, cosmeticItem("&cDragon Flyer",
                "&8Event Join Animation",
                "", "&7When you join a event, a circle will rotate","&7and create cool, mysterious", "&7particles!", "", "&7Rarity: &5EPIC ★", "&7Cost: &215000 &8◄", "", "&aClick to buy this cosmetic!"));

        gui.setItem(20, cosmeticItem("&cCirculator",
                "&8Event Join Animation",
                "", "&7When you join a event, a emerald will go upwards","&7and follow you", "&7then a big emerald ring will cover you!", "", "&7Rarity: &6&lLEGEND ★", "&7Cost: &245000 &8◄", "", "&aClick to buy this cosmetic!"));

        player.openInventory(gui);

        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.getInventory();
        if (!event.getView().getTitle().equals(title)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (event.isShiftClick() || event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        if(clickedItem.getType().equals(Material.BARRIER)){
            player.closeInventory();
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.6f);
        }
    }

    private ItemStack defaultItems(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Arrays.asList(Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toArray(String[]::new)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack cosmeticItem(String name, String... lore) {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + name));
        meta.setLore(Arrays.asList(Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toArray(String[]::new)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
