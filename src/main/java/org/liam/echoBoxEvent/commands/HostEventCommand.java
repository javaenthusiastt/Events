package org.liam.echoBoxEvent.commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.liam.echoBoxEvent.colors.Colors;
import org.liam.echoBoxEvent.players.Data.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class HostEventCommand implements CommandExecutor, Listener {

    private final PlayerData playerData;

    public HostEventCommand(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        openHostMenu(player);
        return true;
    }

    private void openHostMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&8Event Menu"));

        ItemStack sumoItem = createMenuItem(Material.STICK, "&aSumo", false,
                "&8Event", "",
                "&7Battle against other players on a",
                "&7small platform and knock them off!",
                "&7Stay on the platform to be the last",
                "&7one standing and claim victory.",
                "", "&a&lPLAY");
        gui.setItem(12 + 9, sumoItem);


        ItemStack spleefItem = createMenuItem(Material.SNOWBALL, "&aSpleef", false,
                "&8Event", "",
                "&7Use your shovel to dig up the ground",
                "&7beneath your opponents and make them",
                "&7fall into the void below.",
                "&7The last player standing wins!",
                "", "&cDisabled");
        gui.setItem(13 + 9, spleefItem);

        ItemStack questionmark1 = createMenuItem(Material.TNT, "&aTnt Run", false,
                "&8Event", "", "&7Keep running because the blocks under you", "&7will always break, you will have some abilities to save you", "&7Last one to fall in the void wins!", "", "&cDisabled");
        gui.setItem(30 + 9, questionmark1);

        ItemStack questionmark2 = createMenuItem(Material.BARRIER, "&a???", false,
                "&8Event", "", "&cDisabled");
        gui.setItem(30 + 10, questionmark2);

        ItemStack questionmark3 = createMenuItem(Material.BARRIER,"&a???", false,
                "&8Event", "", "&cDisabled");
        gui.setItem(30 + 11, questionmark3);

        ItemStack murderMysteryItem = createMenuItem(Material.BOW, "&aMurder Mystery", true,
                "&8Event", "",
                "&7A thrilling game of deception! Play as",
                "&7an innocent player or the murderer.",
                "&7If you're innocent, survive at all costs.",
                "&7If you're the murderer, eliminate all players",
                "&7without getting caught.",
                "", "&cIn Development");
        gui.setItem(4 + 9, murderMysteryItem);

        ItemStack hideAndSeekItem = createMenuItem(Material.MAP, "&aHide & Seek", false,
                "&8Event", "",
                "&7Classic hide and seek with a twist!",
                "&7Hide as an object in the world and blend in",
                "&7with your surroundings while the seekers",
                "&7try to find and tag you.",
                "", "&cDisabled");
        gui.setItem(30, hideAndSeekItem);

        ItemStack fortuneOfPillarsItem = createMenuItem(Material.ACACIA_DOOR, "&aFortune of Pillars", false,
                "&8Event", "",
                "&7Stand on towering pillars and survive as",
                "&7random items come in your inventory",
                "&7Use these items to outlast your opponents",
                "&7in this exciting and unpredictable event.",
                "", "&cDisabled");
        gui.setItem(14 + 9, fortuneOfPillarsItem);

        ItemStack glassBreakItem = createMenuItem(Material.GLASS, "&aGlass Break", false,
                "&8Event", "",
                "&7Test your courage by jumping across",
                "&7a series of glass platforms! Be careful,",
                "&7as some glass may shatter beneath your feet!",
                "&7Can you make it to the other side?",
                "", "&cDisabled");
        gui.setItem(32, glassBreakItem);

        ItemStack bug = createMenuItem(Material.PAPER, "&aReport Bug", false,
                "&8Bug Report", "",
                "&7Found a bug in the event?",
                "&7Help us improve by reporting it!",
                "&7Your feedback ensures smooth gameplay",
                "&7and helps us fix issues faster.",
                "&7We'll investigate your issue promptly.", "", "&aClick to report a event bug");
        gui.setItem(54 - 9, bug);

        ItemStack playerHead = PlayerHead(player);
        gui.setItem(4, playerHead);

        player.openInventory(gui);
    }

    private ItemStack createMenuItem(Material material, String name, boolean isEnabled, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lore = new ArrayList<>();
        for (String loreLine : loreLines) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }
        meta.setLore(lore);

        if (isEnabled) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        item.setItemMeta(meta);
        return item;
    }



    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8Event Menu"))) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (event.isShiftClick() || event.getClick().isKeyboardClick()) {
                event.setCancelled(true);
                return;
            }

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();
            switch (ChatColor.stripColor(itemName)) {
                case "Sumo":
                    player.performCommand("echoboxsumo");
                    player.closeInventory();
                    break;
                case "Spleef":
                    //player.performCommand("echoboxspleef");
                    player.closeInventory();
                    break;
                case "Murder Mystery":
                   // player.performCommand("echoboxmurdermystery");
                    player.closeInventory();
                    break;
                case "Report Bug":
                    player.performCommand("discord");
                    player.closeInventory();
                    Colors.message(player, "&aJoin our discord and create a ticket to report the bug.");
                    break;
                default:
                    player.closeInventory();
                    break;
            }
        }
    }

    public ItemStack PlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§aYour Event Tokens");
            meta.setOwningPlayer(player);

            playerData.getData(player.getUniqueId());
            int points = playerData.getPoints(player.getUniqueId());

            List<String> lore = new ArrayList<>();
            lore.add("§8Event Tokens");
            lore.add("");
            lore.add("§7Track your current tokens earned from events.");
            lore.add("§7Use them to buy cosmetics or convert to beacons.");
            lore.add("§7Earn tokens by competing in events");
            lore.add("§7Or checking out the new lobby!");
            lore.add("");
            lore.add("§aYou currently got §e"+points+" §atokens!");

            meta.setLore(lore);
            playerHead.setItemMeta(meta);
        }
        return playerHead;
    }
}

