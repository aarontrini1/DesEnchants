package org.example.des.desEnchants.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Map;

public class AnvilListener implements Listener {

    private final DesEnchants plugin;

    public AnvilListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack first = anvil.getItem(0);
        ItemStack second = anvil.getItem(1);

        if (first == null || second == null) return;

        // Case 1: Combining two enchanted books
        if (first.getType() == Material.ENCHANTED_BOOK && second.getType() == Material.ENCHANTED_BOOK) {
            handleBookCombination(event, first, second);
            return;
        }

        // Case 2: Applying enchanted book to item
        if (second.getType() == Material.ENCHANTED_BOOK) {
            handleBookApplication(event, first, second);
            return;
        }

        // Case 3: Combining two items with custom enchantments
        if (first.getType() == second.getType()) {
            handleItemCombination(event, first, second);
        }
    }

    private void handleBookCombination(PrepareAnvilEvent event, ItemStack first, ItemStack second) {
        CustomEnchantment enchant1 = EnchantmentUtils.getEnchantmentFromBook(plugin, first);
        CustomEnchantment enchant2 = EnchantmentUtils.getEnchantmentFromBook(plugin, second);

        if (enchant1 == null || enchant2 == null) return;

        // Must be same enchantment
        if (!enchant1.getId().equals(enchant2.getId())) return;

        int level1 = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, first);
        int level2 = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, second);

        int resultLevel;

        // Same level = combine to next level
        if (level1 == level2) {
            resultLevel = Math.min(level1 + 1, enchant1.getMaxLevel());
            if (resultLevel == level1) return; // Already at max level
        } else {
            // Different levels = take the higher one
            resultLevel = Math.max(level1, level2);
        }

        // Create result book
        ItemStack result = EnchantmentUtils.createEnchantedBook(plugin, enchant1, resultLevel);

        // Copy any dust bonuses from the first book
        copyDustBonuses(first, result);

        // Set result
        event.setResult(result);

        // Calculate cost
        int baseCost = enchant1.getRarity().getXpCost() / 4; // Base cost is 1/4 of purchase cost
        int levelCost = resultLevel * 5;
        event.getInventory().setRepairCost(baseCost + levelCost);
    }

    private void handleBookApplication(PrepareAnvilEvent event, ItemStack item, ItemStack book) {
        CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(plugin, book);
        if (enchantment == null) return;

        // Check if enchantment can be applied
        if (!enchantment.canApplyTo(item)) return;

        // Check if item already has this enchantment
        if (EnchantmentUtils.hasEnchantment(plugin, item, enchantment)) {
            // Check if we can upgrade the level
            int currentLevel = EnchantmentUtils.getEnchantmentLevel(plugin, item, enchantment);
            int bookLevel = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, book);

            if (currentLevel >= enchantment.getMaxLevel()) return;

            int newLevel = (currentLevel == bookLevel) ?
                    Math.min(currentLevel + 1, enchantment.getMaxLevel()) :
                    Math.max(currentLevel, bookLevel);

            if (newLevel == currentLevel) return;

            // Create result with upgraded enchantment
            ItemStack result = item.clone();
            result = EnchantmentUtils.updateEnchantmentLevel(plugin, result, enchantment, newLevel);
            event.setResult(result);
        } else {
            // Check max enchantments
            int currentEnchants = EnchantmentUtils.getEnchantmentCount(plugin, item);
            int maxEnchants = plugin.getConfigManager().getMaxEnchantmentsPerItem();

            if (currentEnchants >= maxEnchants) return;

            // Add the enchantment
            int level = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, book);
            ItemStack result = item.clone();
            result = EnchantmentUtils.addEnchantment(plugin, result, enchantment, level);
            event.setResult(result);
        }

        // Calculate cost
        int baseCost = enchantment.getRarity().getXpCost() / 3;
        event.getInventory().setRepairCost(baseCost);
    }

    private void handleItemCombination(PrepareAnvilEvent event, ItemStack first, ItemStack second) {
        Map<CustomEnchantment, Integer> enchants1 = EnchantmentUtils.getEnchantments(plugin, first);
        Map<CustomEnchantment, Integer> enchants2 = EnchantmentUtils.getEnchantments(plugin, second);

        if (enchants1.isEmpty() && enchants2.isEmpty()) return;

        ItemStack result = first.clone();
        int totalCost = 0;

        // Combine enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : enchants2.entrySet()) {
            CustomEnchantment enchant = entry.getKey();
            int level2 = entry.getValue();

            if (enchants1.containsKey(enchant)) {
                // Combine levels
                int level1 = enchants1.get(enchant);
                int newLevel = (level1 == level2) ?
                        Math.min(level1 + 1, enchant.getMaxLevel()) :
                        Math.max(level1, level2);

                if (newLevel > level1) {
                    result = EnchantmentUtils.updateEnchantmentLevel(plugin, result, enchant, newLevel);
                    totalCost += enchant.getRarity().getXpCost() / 4;
                }
            } else if (enchant.canApplyTo(result)) {
                // Add new enchantment if there's room
                int currentCount = EnchantmentUtils.getEnchantmentCount(plugin, result);
                if (currentCount < plugin.getConfigManager().getMaxEnchantmentsPerItem()) {
                    result = EnchantmentUtils.addEnchantment(plugin, result, enchant, level2);
                    totalCost += enchant.getRarity().getXpCost() / 3;
                }
            }
        }

        if (totalCost > 0) {
            event.setResult(result);
            event.getInventory().setRepairCost(totalCost);
        }
    }

    private void copyDustBonuses(ItemStack source, ItemStack target) {
        // Implementation depends on how dust bonuses are stored
        // This is a placeholder
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvilClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getSlot() != 2) return; // Result slot

        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) return;

        // Check if this is a custom enchantment operation
        if (EnchantmentUtils.getEnchantmentCount(plugin, result) > 0 ||
                EnchantmentUtils.getEnchantmentFromBook(plugin, result) != null) {

            Player player = (Player) event.getWhoClicked();
            AnvilInventory anvil = (AnvilInventory) event.getInventory();

            // Check XP
            if (player.getLevel() < anvil.getRepairCost()) {
                event.setCancelled(true);
                plugin.getLanguageManager().sendMessage(player, "gui.purchase.not-enough-levels",
                        anvil.getRepairCost(), "complete");
                return;
            }

            // Let vanilla handle the rest
        }
    }
}