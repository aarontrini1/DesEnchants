package org.example.des.desEnchants.enchantments.weapons.shared;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

import java.util.Arrays;

public class LifestealEnchantment extends CustomEnchantment {

    public LifestealEnchantment(DesEnchants plugin) {
        super(plugin, "lifesteal", "Lifesteal", 3, EnchantmentRarity.RARE, EnchantmentTarget.ALL_WEAPONS);

        // Configure the enchantment
        this.activationChance = 30.0; // 30% chance to activate
        this.cooldown = 0; // No cooldown
        this.description = Arrays.asList(
                "§7Chance to steal health from",
                "§7your enemies when attacking.",
                "",
                "§7Chance: §a" + activationChance + "%",
                "§7Heal Amount: §a{level} HP"
        );
    }

    @Override
    public boolean canApplyTo(ItemStack item) {
        // Only allow on swords and axes
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE");
    }

    @Override
    public boolean onTrigger(Event event, Player player, ItemStack item, int level) {
        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
            return false;
        }

        // Make sure player is the attacker
        if (damageEvent.getDamager() != player) {
            return false;
        }

        // Check if target is living entity
        if (!(damageEvent.getEntity() instanceof LivingEntity)) {
            return false;
        }

        // Check activation chance
        if (!canActivate(player)) {
            return false;
        }

        // Calculate heal amount (1 HP per level)
        double healAmount = level;
        double damage = damageEvent.getFinalDamage();

        // Can't steal more than damage dealt
        healAmount = Math.min(healAmount, damage);

        // Heal the player
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double currentHealth = player.getHealth();
        double newHealth = Math.min(currentHealth + healAmount, maxHealth);
        player.setHealth(newHealth);

        // Visual effects
        player.getWorld().spawnParticle(Particle.HEART,
                player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);

        // Sound effect
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);

        // Apply cooldown if any
        applyCooldown(player);

        return true;
    }
}