package org.example.des.desEnchants.enchantments.tools.pickaxe;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

import java.util.Arrays;

public class HasteEnchantment extends CustomEnchantment implements Listener {

    public HasteEnchantment(DesEnchants plugin) {
        super(plugin, "haste", "Haste", 3, EnchantmentRarity.COMMON, EnchantmentTarget.PICKAXE, Arrays.asList(
                "§7Gives haste effect while",
                "§7mining with this pickaxe.",
                "",
                "§7Haste Level: §a{level}"
        ));
    }

    public String getLevelSpecificDescription(int level) {
        return "§7Heal: §a10 HP §7Cooldown: §a5 minutes";
    }

    @EventHandler
    public boolean onTrigger(Event event, Player player, ItemStack item, int level) {
        if (!(event instanceof BlockBreakEvent)) {
            return false;
        }

        // Apply haste effect for 5 seconds
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                100, // 5 seconds
                level - 1, // Potion level (0-indexed)
                true, // Ambient
                false, // Particles
                true // Icon
        ));

        return true;
    }

    @Override
    public void initialize() {

    }
}