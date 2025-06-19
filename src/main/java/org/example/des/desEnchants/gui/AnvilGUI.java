package org.example.des.desEnchants.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;
import org.example.des.desEnchants.core.utils.ItemBuilder;

import java.util.Arrays;

public class AnvilGUI {

    private final DesEnchants plugin;
    private final Player player;
    private Inventory inventory;

    // Slots
    private static final int FIRST_SLOT = 11;
    private static final int SECOND_SLOT = 15;
    private static final int RESULT_SLOT = 22;
    private static final int COMBINE_BUTTON = 31;

    public AnvilGUI(DesEnchants plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        setupGUI();
    }

    private void setupGUI() {
        inventory = Bukkit.createInventory(null, 54,
                ChatColor.translateAlternateColorCodes('&', "&6&l✦ Enchantment Anvil ✦"));

        // Fill with glass
        ItemStack glass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, glass);
        }

        // Clear input/output slots
        inventory.setItem(FIRST_SLOT, null);
        inventory.setItem(SECOND_SLOT, null);
        inventory.setItem(RESULT_SLOT, null);

        // Add slot indicators
        ItemStack firstIndicator = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name("&7First Enchantment Book")
                .lore("&7Place an enchantment", "&7book here")
                .build();
        inventory.setItem(FIRST_SLOT - 9, firstIndicator);

        ItemStack secondIndicator = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name("&7Second Enchantment Book")
                .lore("&7Place a matching", "&7enchantment book here")
                .build();
        inventory.setItem(SECOND_SLOT - 9, secondIndicator);

        // Add combine button (disabled initially)
        updateCombineButton();

        // Back button
        ItemStack back = new ItemBuilder(Material.ARROW)
                .name("&cBack to Main Menu")
                .build();
        inventory.setItem(45, back);
    }

    public void handleClick(int slot, ItemStack cursor) {
        // Handle placing items in slots
        if (slot == FIRST_SLOT || slot == SECOND_SLOT) {
            // Allow placing/taking items
            updateCombineButton();
            return;
        }

        // Handle combine button
        if (slot == COMBINE_BUTTON) {
            attemptCombine();
            return;
        }

        // Handle result slot
        if (slot == RESULT_SLOT) {
            ItemStack result = inventory.getItem(RESULT_SLOT);
            if (result != null) {
                // Give result to player
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(result);
                    inventory.setItem(RESULT_SLOT, null);
                    inventory.setItem(FIRST_SLOT, null);
                    inventory.setItem(SECOND_SLOT, null);
                    updateCombineButton();

                    // Play sound
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                } else {
                    plugin.getLanguageManager().sendMessage(player, "gui.purchase.inventory-full");
                }
            }
            return;
        }

        // Handle back button
        if (slot == 45) {
            player.closeInventory();
            new MainEnchantmentGUI(plugin, player).open();
        }
    }

    public void updateCombineButton() {
        ItemStack first = inventory.getItem(FIRST_SLOT);
        ItemStack second = inventory.getItem(SECOND_SLOT);

        if (canCombine(first, second)) {
            // Calculate cost
            int cost = calculateCombineCost(first, second);

            ItemStack combineButton = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .name("&a&lCOMBINE")
                    .lore(
                            "&7Click to combine these",
                            "&7enchantment books!",
                            "",
                            "&7Cost: &a" + cost + " Levels"
                    )
                    .glow()
                    .build();
            inventory.setItem(COMBINE_BUTTON, combineButton);

            // Show preview
            showPreview(first, second);
        } else {
            ItemStack disabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .name("&c&lCANNOT COMBINE")
                    .lore(
                            "&7Place two identical",
                            "&7enchantment books to",
                            "&7combine them!"
                    )
                    .build();
            inventory.setItem(COMBINE_BUTTON, disabled);
            inventory.setItem(RESULT_SLOT, null);
        }
    }

    private boolean canCombine(ItemStack first, ItemStack second) {
        if (first == null || second == null) return false;
        if (first.getType() != Material.ENCHANTED_BOOK || second.getType() != Material.ENCHANTED_BOOK) return false;

        CustomEnchantment enchant1 = EnchantmentUtils.getEnchantmentFromBook(plugin, first);
        CustomEnchantment enchant2 = EnchantmentUtils.getEnchantmentFromBook(plugin, second);

        if (enchant1 == null || enchant2 == null) return false;
        if (!enchant1.getId().equals(enchant2.getId())) return false;

        int level1 = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, first);
        int level2 = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, second);

        // Can only combine same levels
        if (level1 != level2) return false;

        // Check max level
        return level1 < enchant1.getMaxLevel();
    }

    private int calculateCombineCost(ItemStack first, ItemStack second) {
        CustomEnchantment enchant = EnchantmentUtils.getEnchantmentFromBook(plugin, first);
        int level = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, first);

        // Base cost on rarity and level
        int baseCost = enchant.getRarity().getXpCost();
        return baseCost + (level * 10);
    }

    private void showPreview(ItemStack first, ItemStack second) {
        CustomEnchantment enchant = EnchantmentUtils.getEnchantmentFromBook(plugin, first);
        int level = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, first);

        // Create result book with increased level
        ItemStack result = EnchantmentUtils.createEnchantedBook(plugin, enchant, level + 1);
        inventory.setItem(RESULT_SLOT, result);
    }

    private void attemptCombine() {
        ItemStack first = inventory.getItem(FIRST_SLOT);
        ItemStack second = inventory.getItem(SECOND_SLOT);

        if (!canCombine(first, second)) return;

        int cost = calculateCombineCost(first, second);
        if (player.getLevel() < cost) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.not-enough-levels", cost, "combine");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Take levels
        player.setLevel(player.getLevel() - cost);

        // Items are consumed when result is taken
        updateCombineButton();

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
    }

    public void open() {
        plugin.getGuiManager().setOpenGUI(player, "anvil");
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}