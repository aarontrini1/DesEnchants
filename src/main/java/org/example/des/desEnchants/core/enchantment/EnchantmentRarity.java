package org.example.des.desEnchants.core.enchantment;

import org.bukkit.ChatColor;

public enum EnchantmentRarity {
    COMMON(ChatColor.GRAY, "Common", 20),
    RARE(ChatColor.AQUA, "Rare", 40),
    LEGENDARY(ChatColor.GOLD, "Legendary", 60);

    private final ChatColor color;
    private final String displayName;
    private final int xpCost;

    EnchantmentRarity(ChatColor color, String displayName, int xpCost) {
        this.color = color;
        this.displayName = displayName;
        this.xpCost = xpCost;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getXpCost() {
        return xpCost;
    }

    public String format(String text) {
        return color + text;
    }
}