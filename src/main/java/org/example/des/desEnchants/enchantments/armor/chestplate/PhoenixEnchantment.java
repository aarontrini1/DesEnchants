package org.example.des.desEnchants.enchantments.armor.chestplate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhoenixEnchantment extends CustomEnchantment implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownTime = 300000; // 5 minutes in milliseconds

    public PhoenixEnchantment(DesEnchants plugin) {
        super(plugin, "phoenix", "&6Phoenix", 3,
                EnchantmentRarity.LEGENDARY,
                EnchantmentTarget.CHESTPLATE,
                Arrays.asList(
                        "&7Chance to revive with full health",
                        "&7and temporary buffs upon death.",
                        "&7Cooldown: 5 minutes"
                ));
    }

    @Override
    public void initialize() {
        // Any initialization code specific to this enchantment
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack chestplate = player.getInventory().getChestplate();

        if (chestplate == null) return;

        int level = plugin.getEnchantmentManager().getEnchantmentLevel(chestplate, this);
        if (level == 0) return;

        // Check cooldown
        UUID uuid = player.getUniqueId();
        if (cooldowns.containsKey(uuid)) {
            long timeLeft = cooldowns.get(uuid) - System.currentTimeMillis();
            if (timeLeft > 0) {
                return; // Still on cooldown
            }
        }

        // Check activation chance
        if (!shouldActivate(level)) return;

        // Cancel death
        event.setCancelled(true);

        // Revive player
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);

        // Apply effects based on level
        int duration = 200 + (level * 100); // 10-25 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, level - 1));

        if (level >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, 1));
        }

        if (level >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0));
        }

        // Set cooldown
        cooldowns.put(uuid, System.currentTimeMillis() + cooldownTime);

        // Send message
        player.sendMessage(plugin.getLanguageManager().getMessage("phoenix-activated"));
    }
}