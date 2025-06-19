package org.example.des.desEnchants.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Map;

public class PlayerInteractListener implements Listener {

    private final DesEnchants plugin;

    public PlayerInteractListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            return;
        }

        // Get enchantments
        Map<CustomEnchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(plugin, item);
        if (enchantments.isEmpty()) {
            return;
        }

        // Trigger enchantments
        for (Map.Entry<CustomEnchantment, Integer> entry : enchantments.entrySet()) {
            CustomEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (enchantment.isEnabled()) {
                enchantment.onTrigger(event, player, item, level);
            }
        }
    }
}