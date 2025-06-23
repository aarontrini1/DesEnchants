package org.example.des.desEnchants.enchantments.weapons.shared;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

import java.util.Arrays;

public class LightningEnchantment extends CustomEnchantment {

    public LightningEnchantment(DesEnchants plugin) {
        super(plugin, "lightning", "Lightning", 3, EnchantmentRarity.LEGENDARY, EnchantmentTarget.ALL_WEAPONS);

        this.cooldown = 5; // 5 second cooldown
        this.description = Arrays.asList(
                "§7Chance to strike your",
                "§7enemies with lightning."
        );
    }

    @Override
    public String getLevelSpecificDescription(int level) {
        int chance = level * 10; // 10%, 20%, 30%
        return "§7Chance: §a" + chance + "% §7Damage: §a" + (level * 2) + " HP";
    }

    @Override
    public boolean canApplyTo(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE")
                || type == Material.BOW || type == Material.TRIDENT;
    }

    @Override
    public boolean onTrigger(Event event, Player player, ItemStack item, int level) {
        if (!(event instanceof EntityDamageByEntityEvent damageEvent)) {
            return false;
        }

        if (damageEvent.getDamager() != player) {
            return false;
        }

        if (!(damageEvent.getEntity() instanceof LivingEntity target)) {
            return false;
        }

        // Check activation chance based on level
        double chance = level * 10.0; // 10% per level
        if (Math.random() * 100 > chance) {
            return false;
        }

        // Check cooldown
        if (!canActivate(player)) {
            return false;
        }

        // Strike lightning
        Location loc = target.getLocation();
        loc.getWorld().strikeLightningEffect(loc);

        // Deal extra damage
        double extraDamage = level * 2.0;
        target.damage(extraDamage);

        // Apply cooldown
        applyCooldown(player);

        return true;
    }
}