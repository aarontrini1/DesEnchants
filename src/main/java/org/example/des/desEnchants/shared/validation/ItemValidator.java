package org.example.des.desEnchants.shared.validation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

public class ItemValidator {

    /**
     * Check if an item is a valid enchantable item
     */
    public static boolean isEnchantable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        // Check if it's a valid equipment piece
        return isArmor(item) || isWeapon(item) || isTool(item);
    }

    /**
     * Check if item is armor
     */
    public static boolean isArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS") ||
                type == Material.ELYTRA ||
                type == Material.TURTLE_HELMET;
    }

    /**
     * Check if item is a weapon
     */
    public static boolean isWeapon(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type == Material.TRIDENT ||
                type == Material.BOW ||
                type == Material.CROSSBOW;
    }

    /**
     * Check if item is a tool
     */
    public static boolean isTool(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_PICKAXE") ||
                type.name().endsWith("_AXE") ||
                type.name().endsWith("_SHOVEL") ||
                type.name().endsWith("_HOE") ||
                type == Material.SHEARS ||
                type == Material.FISHING_ROD ||
                type == Material.FLINT_AND_STEEL;
    }

    /**
     * Get the enchantment target for an item
     */
    public static EnchantmentTarget getTarget(ItemStack item) {
        Material type = item.getType();

        // Armor
        if (type.name().endsWith("_HELMET") || type == Material.TURTLE_HELMET) {
            return EnchantmentTarget.HELMET;
        }
        if (type.name().endsWith("_CHESTPLATE") || type == Material.ELYTRA) {
            return EnchantmentTarget.CHESTPLATE;
        }
        if (type.name().endsWith("_LEGGINGS")) {
            return EnchantmentTarget.LEGGINGS;
        }
        if (type.name().endsWith("_BOOTS")) {
            return EnchantmentTarget.BOOTS;
        }

        // Weapons
        if (type.name().endsWith("_SWORD")) {
            return EnchantmentTarget.SWORD;
        }
        if (type == Material.BOW || type == Material.CROSSBOW) {
            return EnchantmentTarget.BOW;
        }
        if (type == Material.TRIDENT) {
            return EnchantmentTarget.TRIDENT;
        }

        // Tools
        if (type.name().endsWith("_PICKAXE")) {
            return EnchantmentTarget.PICKAXE;
        }
        if (type.name().endsWith("_SHOVEL")) {
            return EnchantmentTarget.SHOVEL;
        }
        if (type.name().endsWith("_HOE")) {
            return EnchantmentTarget.HOE;
        }
        if (type.name().endsWith("_AXE")) {
            return EnchantmentTarget.AXE;
        }

        return null;
    }

    /**
     * Check if item material matches quality tier
     */
    public static boolean isQualityTier(ItemStack item, String tier) {
        String typeName = item.getType().name();
        return switch (tier.toUpperCase()) {
            case "WOOD", "WOODEN" -> typeName.contains("WOODEN_") || typeName.contains("WOOD_");
            case "STONE" -> typeName.contains("STONE_");
            case "IRON" -> typeName.contains("IRON_");
            case "GOLD", "GOLDEN" -> typeName.contains("GOLDEN_") || typeName.contains("GOLD_");
            case "DIAMOND" -> typeName.contains("DIAMOND_");
            case "NETHERITE" -> typeName.contains("NETHERITE_");
            default -> true;
        };
    }
}