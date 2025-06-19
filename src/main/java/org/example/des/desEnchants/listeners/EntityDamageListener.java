package org.example.des.desEnchants.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;

import java.util.Map;

public class EntityDamageListener implements Listener {

    private final DesEnchants plugin;

    public EntityDamageListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Handle attacker enchantments
        if (event.getDamager() instanceof Player attacker) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();
            triggerEnchantments(event, attacker, weapon);
        }

        // Handle projectile enchantments
        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                ItemStack bow = shooter.getInventory().getItemInMainHand();
                triggerEnchantments(event, shooter, bow);
            }
        }

        // Handle defender enchantments
        if (event.getEntity() instanceof Player defender) {
            // Check armor pieces
            for (ItemStack armor : defender.getInventory().getArmorContents()) {
                if (armor != null) {
                    triggerEnchantments(event, defender, armor);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Trigger armor enchantments for any damage
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                triggerEnchantments(event, player, armor);
            }
        }
    }

    private void triggerEnchantments(EntityDamageEvent event, Player player, ItemStack item) {
        Map<CustomEnchantment, Integer> enchantments = EnchantmentUtils.getEnchantments(plugin, item);

        for (Map.Entry<CustomEnchantment, Integer> entry : enchantments.entrySet()) {
            CustomEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (enchantment.isEnabled()) {
                enchantment.onTrigger(event, player, item, level);
            }
        }
    }
}