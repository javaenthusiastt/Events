package org.liam.echoBoxEvent.cosmetics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestAbilitiesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!(player.getName().equalsIgnoreCase("sorryplspls") || player.getName().equalsIgnoreCase("sqeakre"))) {
            player.sendMessage("&cThis is currently disabled.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("parrot")) {
                CosmeticManager.ParrotCosmetic(player);
            } else if (args[0].equalsIgnoreCase("pumpkin")) {
                CosmeticManager.PumpkinCosmetic(player);
            } else if (args[0].equalsIgnoreCase("firework")) {
                CosmeticManager.FireworksCosmetic(player);
            } else if (args[0].equalsIgnoreCase("aura")) {
                CosmeticManager.AuraCosmetic(player);
            } else if (args[0].equalsIgnoreCase("shadow")) {
                CosmeticManager.ShadowCloakCosmetic(player);
            } else if (args[0].equalsIgnoreCase("rainbow")) {
                CosmeticManager.RainbowEffectCosmetic(player);
            } else if (args[0].equalsIgnoreCase("snowstorm")) {
                CosmeticManager.SnowstormCosmetic(player);
            } else if (args[0].equalsIgnoreCase("shield")) {
                CosmeticManager.MagicShieldCosmetic(player);
            } else if (args[0].equalsIgnoreCase("fireball")) {
                CosmeticManager.Temporary1(player);
            } else if (args[0].equalsIgnoreCase("galaxy")) {
                CosmeticManager.GalaxyOrbitalCosmetic(player);
            } else if (args[0].equalsIgnoreCase("vortex")) {
                CosmeticManager.GlowingCometCosmetic(player);
            } else if (args[0].equalsIgnoreCase("dragon")) {
                CosmeticManager.DragonBreathStorm(player);
            } else if (args[0].equalsIgnoreCase("raindropper")) {
                CosmeticManager.RainDropper(player);
            } else if (args[0].equalsIgnoreCase("heart")) {
                CosmeticManager.DetailedHeartShape(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown cosmetic type."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease specify a valid cosmetic type."));
        }

        return true;
    }
}
