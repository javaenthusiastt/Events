package org.liam.echoBoxEvent.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CreateItems {

    public static ItemStack createItem(Material material, String displayName, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (displayName != null && !displayName.isEmpty()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }

            if (loreLines != null && loreLines.length > 0) {
                List<String> lore = new ArrayList<>();
                for (String line : loreLines) {
                    if (line != null) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                }
                meta.setLore(lore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}
