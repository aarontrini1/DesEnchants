package org.example.des.desEnchants.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.DustType;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;
import org.example.des.desEnchants.enchantments.tools.pickaxe.HasteEnchantment;
import org.example.des.desEnchants.enchantments.weapons.shared.LifestealEnchantment;

import java.util.Random;

public class EnchantmentListener implements Listener {

    private final DesEnchants plugin;
    private final Random random = new Random();

    public EnchantmentListener(DesEnchants plugin) {
        this.plugin = plugin;
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only handle drag-and-drop in player inventory
        if (event.getInventory().getType() != InventoryType.CRAFTING &&
                event.getInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        // Check if drag-and-drop is enabled
        if (!plugin.getConfig().getBoolean("enchanting.drag-drop", true)) {
            return;
        }

        // We're looking for when a player clicks an item with another item on cursor
        if (event.getAction() != InventoryAction.SWAP_WITH_CURSOR &&
                event.getAction() != InventoryAction.PLACE_ALL) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor(); // The enchanted book
        ItemStack current = event.getCurrentItem(); // The item to enchant

        if (cursor == null || current == null ||
                cursor.getType() != Material.ENCHANTED_BOOK ||
                current.getType() == Material.AIR) {
            return;
        }

        // Check if cursor is a custom enchanted book
        CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(plugin, cursor);
        if (enchantment == null) {
            return;
        }

        int level = EnchantmentUtils.getEnchantmentLevelFromBook(plugin, cursor);

        // Cancel the event
        event.setCancelled(true);

        // Check if enchantment can be applied to this item
        if (!EnchantmentUtils.canApplyEnchantment(current, enchantment)) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.invalid-item");
            return;
        }

        // Check max enchantments
        int currentEnchants = EnchantmentUtils.getEnchantmentCount(plugin, current);
        int maxEnchants = plugin.getConfigManager().getMaxEnchantmentsPerItem();

        if (currentEnchants >= maxEnchants) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.max-enchants");
            return;
        }

        // Check if item already has this enchantment
        if (EnchantmentUtils.hasEnchantment(plugin, current, enchantment)) {
            plugin.getLanguageManager().sendMessage(player, "enchanting.already-has");
            return;
        }

        // Calculate success/destroy chances
        int successRate = enchantment.getSuccessRate();
        int destroyRate = enchantment.getDestroyRate();

        // Roll the dice
        int roll = random.nextInt(100) + 1;

        if (roll <= successRate) {
            // Success! Apply the enchantment
            ItemStack enchantedItem = EnchantmentUtils.addEnchantment(plugin, current, enchantment, level);
            event.setCurrentItem(enchantedItem);

            // Remove the book
            player.setItemOnCursor(null);

            // Effects
            plugin.getLanguageManager().sendMessage(player, "enchanting.success",
                    enchantment.getRarity().format(enchantment.getDisplayName()));

            // Particles
            if (plugin.getConfig().getBoolean("enchanting.show-particles", true)) {
                player.getWorld().spawnParticle(Particle.ENCHANT,
                        player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            }

            // Sound
            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.enchant-success", "BLOCK_ANVIL_USE"));
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {}

        } else if (roll > (100 - destroyRate)) {
            // Destroyed!
            event.setCurrentItem(null);
            player.setItemOnCursor(null);

            plugin.getLanguageManager().sendMessage(player, "enchanting.destroyed");

            // Sound
            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.enchant-fail", "ENTITY_ITEM_BREAK"));
                player.playSound(player.getLocation(), sound, 1.0f, 0.5f);
            } catch (IllegalArgumentException ignored) {}

        } else {
            // Failed but not destroyed
            player.setItemOnCursor(null);

            plugin.getLanguageManager().sendMessage(player, "enchanting.failed");

            // Sound
            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.enchant-fail", "ENTITY_ITEM_BREAK"));
                player.playSound(player.getLocation(), sound, 1.0f, 1.5f);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @EventHandler
    public void onDustApplication(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CRAFTING &&
                event.getInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        if (event.getAction() != InventoryAction.SWAP_WITH_CURSOR &&
                event.getAction() != InventoryAction.PLACE_ALL) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor(); // The dust
        ItemStack current = event.getCurrentItem(); // The book

        if (cursor == null || current == null) return;

        // Check if cursor is dust
        DustType dustType = EnchantmentUtils.getDustType(cursor);
        if (dustType == null) return;

        // Check if current item is enchanted book
        if (current.getType() != Material.ENCHANTED_BOOK) return;

        CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(plugin, current);
        if (enchantment == null) return;

        event.setCancelled(true);

        // Get dust percentage
        int percentage = EnchantmentUtils.getDustPercentage(cursor);

        // Apply dust to book
        ItemStack updatedBook = EnchantmentUtils.applyDustToBook(plugin, current, dustType, percentage);
        event.setCurrentItem(updatedBook);

        // Remove dust
        player.setItemOnCursor(null);

        // Effects
        player.sendMessage(plugin.prefix + dustType.format("Applied " + dustType.getDisplayName() +
                " (+" + percentage + "%) to " + enchantment.getDisplayName() + "!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);

        // Particles
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
    }

}