package org.example.des.desEnchants.enchantments.armor.chestplate;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;
import org.example.des.desEnchants.shared.effects.ParticleHelper;
import org.example.des.desEnchants.shared.effects.SoundHelper;

import java.util.Arrays;

public class PhoenixEnchantment extends CustomEnchantment {

    public PhoenixEnchantment(DesEnchants plugin) {
        super(plugin, "phoenix", "Phoenix", 1, EnchantmentRarity.LEGENDARY, EnchantmentTarget.CHESTPLATE);

        this.activationChance = 100.0; // Always activates when conditions are met
        this.cooldown = 3000; // 5 minute cooldown
        this.description = Arrays.asList(
                "§7Rise from the ashes when",
                "§7you would have died.",
                "",
                "§7Heal Amount: §a10 HP",
                "§7Effects: §aFire Resistance, Regen",
                "§7Cooldown: §a5 minutes"
        );
    }

    @Override
    public String getLevelSpecificDescription(int level) {
        return "§7Heal: §a10 HP §7Cooldown: §a5 minutes";
    }

    @Override
    public boolean onTrigger(Event event, Player player, ItemStack item, int level) {
        if (!(event instanceof EntityDamageEvent damageEvent)) {
            return false;
        }

        // Check if damage would kill the player
        double finalDamage = damageEvent.getFinalDamage();
        double health = player.getHealth();

        if (health - finalDamage > 0.5) {
            return false; // Player won't die
        }

        // Check if can activate
        if (!canActivate(player)) {
            return false;
        }

        // Cancel the death
        damageEvent.setCancelled(true);

        // Set health to 10
        player.setHealth(10.0);

        // Apply effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1)); // 30 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2)); // 10 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 1)); // 30 seconds

        // Visual effects
        ParticleHelper.playHelix(player, Particle.FLAME, 1.5, 2.0, 50);
        ParticleHelper.playBurst(player.getLocation(), Particle.LAVA, 30, 0.5);

        // Sound effects
        SoundHelper.playAtLocation(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.5f);
        SoundHelper.playAtLocation(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

        // Message
        player.sendMessage(plugin.prefix + "§6§lPhoenix §7has saved you from death!");

        // Apply cooldown
        applyCooldown(player);

        return true;
    }
}