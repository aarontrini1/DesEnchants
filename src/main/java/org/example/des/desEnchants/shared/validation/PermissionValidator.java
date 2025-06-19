package org.example.des.desEnchants.shared.validation;

import org.bukkit.entity.Player;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;

public class PermissionValidator {

    private static final String BASE_PERMISSION = "desenchants";

    /**
     * Check if player has permission to use a specific enchantment
     */
    public static boolean canUseEnchantment(Player player, CustomEnchantment enchantment) {
        // Check specific enchantment permission
        if (player.hasPermission(BASE_PERMISSION + ".enchant." + enchantment.getId())) {
            return true;
        }

        // Check rarity permission
        if (player.hasPermission(BASE_PERMISSION + ".rarity." + enchantment.getRarity().name().toLowerCase())) {
            return true;
        }

        // Check wildcard
        return player.hasPermission(BASE_PERMISSION + ".enchant.*");
    }

    /**
     * Check if player has permission to purchase a rarity
     */
    public static boolean canPurchaseRarity(Player player, EnchantmentRarity rarity) {
        return player.hasPermission(BASE_PERMISSION + ".purchase." + rarity.name().toLowerCase()) ||
                player.hasPermission(BASE_PERMISSION + ".purchase.*");
    }

    /**
     * Check if player can bypass max enchantment limit
     */
    public static boolean canBypassLimit(Player player) {
        return player.hasPermission(BASE_PERMISSION + ".bypass.limit");
    }

    /**
     * Check if player can bypass cooldowns
     */
    public static boolean canBypassCooldown(Player player) {
        return player.hasPermission(BASE_PERMISSION + ".bypass.cooldown");
    }

    /**
     * Check if player can bypass destroy chance
     */
    public static boolean canBypassDestroy(Player player) {
        return player.hasPermission(BASE_PERMISSION + ".bypass.destroy");
    }

    /**
     * Get max enchantments for a player
     */
    public static int getMaxEnchantments(Player player, int defaultMax) {
        for (int i = 10; i >= 1; i--) {
            if (player.hasPermission(BASE_PERMISSION + ".max." + i)) {
                return i;
            }
        }
        return defaultMax;
    }
}