package org.example.des.desEnchants.core.enchantment;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum DustType {
    ANGEL_DUST(
            "Angel Dust",
            Material.SUGAR,
            ChatColor.AQUA,
            "Increases success rate",
            5, // XP cost
            3, 16 // Min/Max bonus
    ),
    DEMON_DUST(
            "Demon Dust",
            Material.REDSTONE,
            ChatColor.RED,
            "Decreases destroy rate",
            5, // XP cost
            3, 16 // Min/Max reduction
    );

    private final String displayName;
    private final Material material;
    private final ChatColor color;
    private final String description;
    private final int xpCost;
    private final int minBonus;
    private final int maxBonus;

    DustType(String displayName, Material material, ChatColor color,
             String description, int xpCost, int minBonus, int maxBonus) {
        this.displayName = displayName;
        this.material = material;
        this.color = color;
        this.description = description;
        this.xpCost = xpCost;
        this.minBonus = minBonus;
        this.maxBonus = maxBonus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public int getXpCost() {
        return xpCost;
    }

    public int getMinBonus() {
        return minBonus;
    }

    public int getMaxBonus() {
        return maxBonus;
    }

    public String format(String text) {
        return color + text;
    }
}