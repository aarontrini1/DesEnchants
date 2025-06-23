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

        this.cooldown = 0; // No cooldown
        this.description = Arrays.asList(
                "§7Chance to steal health from",
                "§7your enemies when attacking."
        );
    }

    @Override
    public String getLevelSpecificDescription(int level) {
        int chance = switch (level) {
            case 1 -> 15;
            case 2 -> 30;
            case 3 -> 50;
            default -> level * 15;
        };
        return "§7Chance: §a" + chance + "% §7Heal: §a" + level + " HP";
    }

    @Override
    public boolean canApplyTo(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE");
    }

    @Override
    public boolean onTrigger(Event event, Player player, ItemStack item, int level) {
        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
            return false;
        }

        if (damageEvent.getDamager() != player) {
            return false;
        }

        if (!(damageEvent.getEntity() instanceof LivingEntity)) {
            return false;
        }

        // Calculate actual activation chance based on level
        double actualChance = switch (level) {
            case 1 -> 15.0;
            case 2 -> 30.0;
            case 3 -> 50.0;
            default -> level * 15.0;
        };

        if (Math.random() * 100 > actualChance) {
            return false;
        }

        double healAmount = level;
        double damage = damageEvent.getFinalDamage();
        healAmount = Math.min(healAmount, damage);

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double currentHealth = player.getHealth();
        double newHealth = Math.min(currentHealth + healAmount, maxHealth);
        player.setHealth(newHealth);

        player.getWorld().spawnParticle(Particle.HEART,
                player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);

        applyCooldown(player);
        return true;
    }
}