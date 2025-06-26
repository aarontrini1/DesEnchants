package org.example.des.desEnchants.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Map;

public class EnchantmentListener implements Listener {

    private final DesEnchants plugin;

    public EnchantmentListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantmentApply(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (cursor == null || current == null) return;

        // Check if cursor is an enchantment book
        if (!EnchantmentUtils.isEnchantmentBook(cursor)) return;

        // Get enchantment from book
        CustomEnchantment enchantment = EnchantmentUtils.getEnchantmentFromBook(cursor);
        int level = EnchantmentUtils.getEnchantmentLevelFromBook(cursor);

        if (enchantment == null) return;

        // Check if item can be enchanted
        if (!enchantment.canEnchantItem(current)) {
            player.sendMessage(plugin.getLanguageManager().getMessage("cannot-enchant-item"));
            return;
        }

        // Check max enchantments
        Map<CustomEnchantment, Integer> currentEnchants = plugin.getEnchantmentManager().getEnchantments(current);
        if (currentEnchants.size() >= plugin.getConfigManager().getMaxEnchantments()) {
            player.sendMessage(plugin.getLanguageManager().getMessage("max-enchantments-reached"));
            return;
        }

        event.setCancelled(true);

        // Roll for success/destroy
        int successRate = EnchantmentUtils.getSuccessRate(cursor);
        int destroyRate = EnchantmentUtils.getDestroyRate(cursor);

        int roll = plugin.getEnchantmentManager().getRandom().nextInt(100);

        if (roll < successRate) {
            // Success - apply enchantment
            plugin.getEnchantmentManager().addEnchantment(current, enchantment, level);
            event.setCursor(null); // Clear the cursor
            player.playSound(player.getLocation(), enchantment.getApplySound(), 1f, 1f);
            player.sendMessage(plugin.getLanguageManager().getMessage("enchantment-success",
                    "%enchantment%", enchantment.getDisplayName()));
        } else if (roll < successRate + destroyRate) {
            // Destroy item
            event.setCurrentItem(new ItemStack(Material.AIR));
            event.setCursor(null); // Clear the cursor
            player.sendMessage(plugin.getLanguageManager().getMessage("enchantment-destroyed"));
        } else {
            // Fail
            event.setCursor(null); // Clear the cursor
            player.sendMessage(plugin.getLanguageManager().getMessage("enchantment-failed"));
        }
    }
}