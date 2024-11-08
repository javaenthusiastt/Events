package org.liam.echoBoxEvent.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryHolderManager {

    public static final Map<Player, ItemStack[]> playerInventories = new HashMap<>();
    public static final Map<Player, ItemStack[]> playerArmor = new HashMap<>();
    private static Map<Player, ItemStack> playerOffhand = new HashMap<>();


    public static void restorePlayerInventory(Player player) {
        if (playerInventories.containsKey(player)) {
            player.getInventory().clear();

            ItemStack[] contents = playerInventories.get(player);
            if (contents != null) {
                player.getInventory().setContents(contents);
            }
            playerInventories.remove(player);
        }

        if (playerArmor.containsKey(player)) {
            player.getInventory().setArmorContents(null);

            ItemStack[] armorContents = playerArmor.get(player);
            if (armorContents != null) {
                player.getInventory().setArmorContents(armorContents);
            }
            playerArmor.remove(player);
        }

        if (playerOffhand.containsKey(player)) {
            player.getInventory().setItemInOffHand(null);
            ItemStack offhandItem = playerOffhand.get(player);
            if (offhandItem != null) {
                player.getInventory().setItemInOffHand(offhandItem);
            }
            playerOffhand.remove(player);
        }
    }

    public static void saveInventory(Player player) {
        playerInventories.put(player, player.getInventory().getContents());
        playerArmor.put(player, player.getInventory().getArmorContents());
        playerOffhand.put(player, player.getInventory().getItemInOffHand());
    }
}
