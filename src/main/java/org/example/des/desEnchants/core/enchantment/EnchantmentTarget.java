package org.example.des.desEnchants.core.enchantment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum EnchantmentTarget {

    ALL("All Items", material -> true),

    ARMOR("Armor", material ->
            material.name().endsWith("_HELMET") ||
                    material.name().endsWith("_CHESTPLATE") ||
                    material.name().endsWith("_LEGGINGS") ||
                    material.name().endsWith("_BOOTS")),

    HELMET("Helmet", material -> material.name().endsWith("_HELMET")),

    CHESTPLATE("Chestplate", material -> material.name().endsWith("_CHESTPLATE")),

    LEGGINGS("Leggings", material -> material.name().endsWith("_LEGGINGS")),

    BOOTS("Boots", material -> material.name().endsWith("_BOOTS")),

    SWORD("Sword", material -> material.name().endsWith("_SWORD")),

    AXE("Axe", material -> material.name().endsWith("_AXE")),

    WEAPON("Weapon", material ->
            material.name().endsWith("_SWORD") ||
                    material.name().endsWith("_AXE")),

    TOOL("Tool", material ->
            material.name().endsWith("_PICKAXE") ||
                    material.name().endsWith("_SHOVEL") ||
                    material.name().endsWith("_HOE") ||
                    material.name().endsWith("_AXE")),

    PICKAXE("Pickaxe", material -> material.name().endsWith("_PICKAXE")),

    SHOVEL("Shovel", material -> material.name().endsWith("_SHOVEL")),

    HOE("Hoe", material -> material.name().endsWith("_HOE")),

    BOW("Bow", material ->
            material == Material.BOW ||
                    material == Material.CROSSBOW),

    ELYTRA("Elytra", material -> material == Material.ELYTRA),

    FISHING_ROD("Fishing Rod", material -> material == Material.FISHING_ROD),

    TRIDENT("Trident", material -> material == Material.TRIDENT),

    SHIELD("Shield", material -> material == Material.SHIELD);

    private final String displayName;
    private final MaterialPredicate predicate;

    EnchantmentTarget(String displayName, MaterialPredicate predicate) {
        this.displayName = displayName;
        this.predicate = predicate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canEnchant(ItemStack item) {
        if (item == null) return false;
        return predicate.test(item.getType());
    }

    public boolean canEnchant(Material material) {
        return predicate.test(material);
    }

    @FunctionalInterface
    private interface MaterialPredicate {
        boolean test(Material material);
    }
}