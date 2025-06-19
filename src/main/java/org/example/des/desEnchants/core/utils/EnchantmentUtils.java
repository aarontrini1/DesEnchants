package org.example.des.desEnchants.core.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.DustType;

import java.util.*;

public class EnchantmentUtils {

    /**
     * Get enchantment from an enchanted book
     */
    public static CustomEnchantment getEnchantmentFromBook(DesEnchants plugin, ItemStack book) {
        if (book == null || book.getType() != Material.ENCHANTED_BOOK) {
            return null;
        }

        NBTItem nbtItem = new NBTItem(book);
        if (!nbtItem.hasTag("DesEnchantBook")) {
            return null;
        }

        String enchantId = nbtItem.getCompound("DesEnchantBook").getString("enchantment");
        return plugin.getEnchantmentManager().getEnchantment(enchantId);
    }

    /**
     * Get enchantment level from an enchanted book
     */
    public static int getEnchantmentLevelFromBook(DesEnchants plugin, ItemStack book) {
        if (book == null || book.getType() != Material.ENCHANTED_BOOK) {
            return 0;
        }

        NBTItem nbtItem = new NBTItem(book);
        if (!nbtItem.hasTag("DesEnchantBook")) {
            return 0;
        }

        return nbtItem.getCompound("DesEnchantBook").getInteger("level");
    }

    /**
     * Check if an enchantment can be applied to an item
     */
    public static boolean canApplyEnchantment(ItemStack item, CustomEnchantment enchantment) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return enchantment.canApplyTo(item);
    }

    /**
     * Get the number of custom enchantments on an item
     */
    public static int getEnchantmentCount(DesEnchants plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesEnchants")) {
            return 0;
        }

        return nbtItem.getCompound("DesEnchants").getKeys().size();
    }

    /**
     * Check if an item has a specific enchantment
     */
    public static boolean hasEnchantment(DesEnchants plugin, ItemStack item, CustomEnchantment enchantment) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesEnchants")) {
            return false;
        }

        return nbtItem.getCompound("DesEnchants").hasTag(enchantment.getId());
    }

    /**
     * Add an enchantment to an item
     */
    public static ItemStack addEnchantment(DesEnchants plugin, ItemStack item, CustomEnchantment enchantment, int level) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.getOrCreateCompound("DesEnchants").setInteger(enchantment.getId(), level);

        ItemStack result = nbtItem.getItem();
        updateItemLore(plugin, result);
        return result;
    }

    /**
     * Get all enchantments on an item
     */
    public static Map<CustomEnchantment, Integer> getEnchantments(DesEnchants plugin, ItemStack item) {
        Map<CustomEnchantment, Integer> enchantments = new HashMap<>();

        if (item == null || item.getType() == Material.AIR) {
            return enchantments;
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesEnchants")) {
            return enchantments;
        }

        var compound = nbtItem.getCompound("DesEnchants");
        for (String key : compound.getKeys()) {
            CustomEnchantment enchant = plugin.getEnchantmentManager().getEnchantment(key);
            if (enchant != null) {
                enchantments.put(enchant, compound.getInteger(key));
            }
        }

        return enchantments;
    }

    /**
     * Update item lore to show enchantments
     */
    private static void updateItemLore(DesEnchants plugin, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        // Remove old enchantment lore
        lore.removeIf(line -> line.contains("§7§m-----") || isEnchantmentLine(plugin, line));

        Map<CustomEnchantment, Integer> enchantments = getEnchantments(plugin, item);
        if (!enchantments.isEmpty()) {
            lore.add(0, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");

            int index = 1;
            for (Map.Entry<CustomEnchantment, Integer> entry : enchantments.entrySet()) {
                CustomEnchantment enchant = entry.getKey();
                int level = entry.getValue();

                String enchantLine = enchant.getRarity().format(
                        enchant.getDisplayName() + " " + toRoman(level));
                lore.add(index++, enchantLine);

                // Add description if enabled
                if (plugin.getConfigManager().showEnchantmentDescriptions() &&
                        enchant.getDescription() != null) {
                    for (String descLine : enchant.getDescription()) {
                        lore.add(index++, ChatColor.GRAY + "  " + descLine);
                    }
                }
            }

            lore.add(index, ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private static boolean isEnchantmentLine(DesEnchants plugin, String line) {
        String stripped = ChatColor.stripColor(line);
        for (CustomEnchantment enchant : plugin.getEnchantmentManager().getAllEnchantments()) {
            if (stripped.contains(ChatColor.stripColor(enchant.getDisplayName()))) {
                return true;
            }
        }
        return false;
    }

    private static String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    /**
     * Create an enchanted book item
     */
    public static ItemStack createEnchantedBook(DesEnchants plugin, CustomEnchantment enchantment, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();

        meta.setDisplayName(enchantment.getRarity().format(
                enchantment.getDisplayName() + " " + toRoman(level)));

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");

        // Show success/destroy rates if enabled
        if (plugin.getConfig().getBoolean("enchant-book-settings.show-rates", true)) {
            String rateFormat = plugin.getConfig().getString("enchant-book-settings.rate-format",
                    "&7Success: &a{success}% &7Destroy: &c{destroy}%");
            rateFormat = rateFormat.replace("{success}", String.valueOf(enchantment.getSuccessRate()))
                    .replace("{destroy}", String.valueOf(enchantment.getDestroyRate()));
            lore.add(ChatColor.translateAlternateColorCodes('&', rateFormat));
            lore.add("");
        }

        lore.add(ChatColor.YELLOW + "Max Level: " + ChatColor.WHITE + enchantment.getMaxLevel());
        lore.add("");

        if (enchantment.getDescription() != null) {
            for (String line : enchantment.getDescription()) {
                lore.add(ChatColor.GRAY + line);
            }
            lore.add("");
        }

        lore.add(plugin.getLanguageManager().getMessage("gui.item-lore.drag-drop"));
        lore.add(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------------");

        meta.setLore(lore);
        book.setItemMeta(meta);

        // Store enchantment data in NBT
        NBTItem nbtItem = new NBTItem(book);
        var compound = nbtItem.getOrCreateCompound("DesEnchantBook");
        compound.setString("enchantment", enchantment.getId());
        compound.setInteger("level", level);

        return nbtItem.getItem();
    }

    // Add these methods to EnchantmentUtils.java

    /**
     * Create a dust item
     */
    public static ItemStack createDust(DesEnchants plugin, DustType dustType, int percentage) {
        ItemBuilder builder = new ItemBuilder(dustType.getMaterial())
                .name(dustType.format("✦ " + dustType.getDisplayName() + " ✦"))
                .lore(
                        "&7" + dustType.getDescription(),
                        "",
                        dustType.format("+" + percentage + "%"),
                        "",
                        "&7Drag onto an enchantment book",
                        "&7to apply this dust!",
                        "",
                        "&8Type: " + dustType.name()
                )
                .glow();

        ItemStack dust = builder.build();

        // Store dust data in NBT
        NBTItem nbtItem = new NBTItem(dust);
        var compound = nbtItem.getOrCreateCompound("DesDust");
        compound.setString("type", dustType.name());
        compound.setInteger("percentage", percentage);

        return nbtItem.getItem();
    }

    /**
     * Get dust type from item
     */
    public static DustType getDustType(ItemStack item) {
        if (item == null) return null;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesDust")) return null;

        String typeName = nbtItem.getCompound("DesDust").getString("type");
        try {
            return DustType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get dust percentage from item
     */
    public static int getDustPercentage(ItemStack item) {
        if (item == null) return 0;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesDust")) return 0;

        return nbtItem.getCompound("DesDust").getInteger("percentage");
    }

    /**
     * Apply dust to an enchanted book
     */
    public static ItemStack applyDustToBook(DesEnchants plugin, ItemStack book, DustType dustType, int percentage) {
        NBTItem nbtItem = new NBTItem(book);
        var bookData = nbtItem.getOrCreateCompound("DesEnchantBook");

        if (dustType == DustType.ANGEL_DUST) {
            int current = bookData.getInteger("angelDustBonus");
            bookData.setInteger("angelDustBonus", current + percentage);
        } else {
            int current = bookData.getInteger("demonDustReduction");
            bookData.setInteger("demonDustReduction", current + percentage);
        }

        ItemStack result = nbtItem.getItem();

        // Update lore to show dust applied
        ItemMeta meta = result.getItemMeta();
        List<String> lore = meta.getLore();

        // Find the success/destroy rate line and update it
        for (int i = 0; i < lore.size(); i++) {
            String line = ChatColor.stripColor(lore.get(i));
            if (line.contains("Success:") && line.contains("Destroy:")) {
                CustomEnchantment enchant = getEnchantmentFromBook(plugin, result);
                int angelBonus = bookData.getInteger("angelDustBonus");
                int demonReduction = bookData.getInteger("demonDustReduction");

                int finalSuccess = Math.min(100, enchant.getSuccessRate() + angelBonus);
                int finalDestroy = Math.max(0, enchant.getDestroyRate() - demonReduction);

                String rateFormat = plugin.getConfig().getString("enchant-book-settings.rate-format",
                        "&7Success: &a{success}% &7Destroy: &c{destroy}%");
                rateFormat = rateFormat.replace("{success}", String.valueOf(finalSuccess))
                        .replace("{destroy}", String.valueOf(finalDestroy));

                // Add dust indicators
                if (angelBonus > 0) {
                    rateFormat += " &b(+" + angelBonus + "%)";
                }
                if (demonReduction > 0) {
                    rateFormat += " &c(-" + demonReduction + "%)";
                }

                lore.set(i, ChatColor.translateAlternateColorCodes('&', rateFormat));
                break;
            }
        }

        meta.setLore(lore);
        result.setItemMeta(meta);

        return result;
    }

    /**
     * Get enchantment level from item
     */
    public static int getEnchantmentLevel(DesEnchants plugin, ItemStack item, CustomEnchantment enchantment) {
        Map<CustomEnchantment, Integer> enchants = getEnchantments(plugin, item);
        return enchants.getOrDefault(enchantment, 0);
    }

    /**
     * Update enchantment level on item
     */
    public static ItemStack updateEnchantmentLevel(DesEnchants plugin, ItemStack item, CustomEnchantment enchantment, int newLevel) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("DesEnchants")) {
            return addEnchantment(plugin, item, enchantment, newLevel);
        }

        nbtItem.getCompound("DesEnchants").setInteger(enchantment.getId(), newLevel);
        ItemStack result = nbtItem.getItem();
        updateItemLore(plugin, result);
        return result;
    }
}