package org.example.des.desEnchants.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.DustType;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Random;

public class EnchantmentListener implements Listener {

    private final DesEnchants plugin;
    private final Random random = new Random();

    public EnchantmentListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemStack book = event.getCursor();

        // Skip if no items or in creative/custom inventory
        if (item == null || book == null || book.getAmount() > 1 || item.getAmount() > 1) return;
        if (event.getInventory().getType() == InventoryType.CREATIVE) return;
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) return;

        // Check if drag-drop is enabled
        if (!plugin.getConfig().getBoolean("enchanting.drag-drop", true)) return;

        // Handle enchanted book application
        if (book.getType() == Material.ENCHANTED_BOOK && !EnchantmentUtils.isEnchantmentBook(plugin, item)) {
            CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(plugin, book);
            if (enchantment != null) {
                handleEnchantmentApplication(event, player, book, item, enchantment);
                return;
            }
        }

        // Handle dust application to book
        DustType dustType = EnchantmentUtils.getDustType(book);
        if (dustType != null && item.getType() == Material.ENCHANTED_BOOK) {
            handleDustApplication(event, player, book, item, dustType);
        }
    }

    private void handleEnchantmentApplication(InventoryClickEvent event, Player player, ItemStack book, ItemStack item, CustomEnchantment enchantment) {
        int level = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, book);

        // Validation checks
        if (!EnchantmentUtils.canApplyEnchantment(item, enchantment)) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.invalid-item");
            return;
        }

        int currentEnchants = EnchantmentUtils.getEnchantmentCount(plugin, item);
        int maxEnchants = plugin.getConfigManager().getMaxEnchantmentsPerItem();

        if (currentEnchants >= maxEnchants) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.max-enchants");
            return;
        }

        if (EnchantmentUtils.hasEnchantment(plugin, item, enchantment)) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.already-has");
            return;
        }

        // Cancel the event now that we know we're processing it
        event.setCancelled(true);

        // Get success/destroy rates
        int baseSuccessRate = EnchantmentUtils.getBookSuccessRate(plugin, book);
        int baseDestroyRate = EnchantmentUtils.getBookDestroyRate(plugin, book);

        int angelBonus = EnchantmentUtils.getAngelDustBonus(plugin, book);
        int demonReduction = EnchantmentUtils.getDemonDustReduction(plugin, book);

        int finalSuccessRate = Math.min(100, baseSuccessRate + angelBonus);
        int finalDestroyRate = Math.max(0, baseDestroyRate - demonReduction);

        int roll = random.nextInt(100) + 1;

        if (roll <= finalSuccessRate) {
            // Success! Clone the item and add enchantment
            ItemStack enchantedItem = item.clone();
            EnchantmentUtils.addEnchantment(plugin, enchantedItem, enchantment, level);

            // Set the enchanted item in the slot
            event.setCurrentItem(enchantedItem);

            // Remove the book from cursor
            player.setItemOnCursor(null);

            // Effects
            plugin.getLanguageManager().sendMessage(player, "enchanting.success",
                    enchantment.getRarity().format(enchantment.getDisplayName()));

            if (plugin.getConfig().getBoolean("enchanting.show-particles", true)) {
                player.getWorld().spawnParticle(Particle.ENCHANT,
                        player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);

        } else if (roll > (100 - finalDestroyRate)) {
            // Destroyed!
            event.setCurrentItem(null);
            player.setItemOnCursor(null);

            // Effects
            plugin.getLanguageManager().sendMessage(player, "enchanting.destroyed");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);

            if (plugin.getConfig().getBoolean("enchanting.show-particles", true)) {
                player.getWorld().spawnParticle(Particle.SMOKE,
                        player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            }

        } else {
            // Failed - only remove the book
            player.setItemOnCursor(null);

            // Effects
            plugin.getLanguageManager().sendMessage(player, "enchanting.failed");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.5f);
        }
    }

    private void handleDustApplication(InventoryClickEvent event, Player player, ItemStack dust, ItemStack book, DustType dustType) {
        CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(plugin, book);
        if (enchantment == null) return;

        event.setCancelled(true);

        int percentage = EnchantmentUtils.getDustPercentage(dust);

        // Clone the book and apply dust
        ItemStack updatedBook = book.clone();
        EnchantmentUtils.applyDustToBook(plugin, updatedBook, dustType, percentage);

        // Set the updated book in the slot
        event.setCurrentItem(updatedBook);

        // Remove dust from cursor
        player.setItemOnCursor(null);

        // Effects
        player.sendMessage(plugin.prefix + dustType.format("Applied " + dustType.getDisplayName() +
                " (+" + percentage + "%) to " + enchantment.getDisplayName() + "!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
    }
}