package org.example.des.desEnchants.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Map;

public class BlockBreakListener implements Listener {

    private final DesEnchants plugin;

    public BlockBreakListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Ignore creative mode
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) {
            return;
        }

        // Get all enchantments on the item
        Map<CustomEnchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(plugin, item);
        if (enchantments.isEmpty()) {
            return;
        }

        // Trigger each enchantment
        for (Map.Entry<CustomEnchantment, Integer> entry : enchantments.entrySet()) {
            CustomEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            // Only trigger if enchantment is enabled
            if (enchantment.isEnabled()) {
                enchantment.onTrigger(event, player, item, level);
            }
        }
    }
}