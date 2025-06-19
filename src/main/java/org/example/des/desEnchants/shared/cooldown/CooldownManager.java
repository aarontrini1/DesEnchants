package org.example.des.desEnchants.shared.cooldown;

import org.bukkit.entity.Player;
import org.example.des.desEnchants.DesEnchants;

import java.util.*;

public class CooldownManager {

    private final DesEnchants plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public CooldownManager(DesEnchants plugin) {
        this.plugin = plugin;
    }

    /**
     * Set a cooldown for a player and enchantment
     * @param player The player
     * @param enchantmentId The enchantment ID
     * @param seconds The cooldown duration in seconds
     */
    public void setCooldown(Player player, String enchantmentId, int seconds) {
        UUID uuid = player.getUniqueId();

        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(enchantmentId, System.currentTimeMillis() + (seconds * 1000L));
    }

    /**
     * Check if a player can use an enchantment (not on cooldown)
     * @param player The player
     * @param enchantmentId The enchantment ID
     * @return true if the player can use the enchantment
     */
    public boolean isOnCooldown(Player player, String enchantmentId) {
        UUID uuid = player.getUniqueId();

        if (!cooldowns.containsKey(uuid)) {
            return false;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(enchantmentId)) {
            return false;
        }

        long cooldownEnd = playerCooldowns.get(enchantmentId);
        if (System.currentTimeMillis() >= cooldownEnd) {
            // Cooldown expired, remove it
            playerCooldowns.remove(enchantmentId);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(uuid);
            }
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in seconds
     * @param player The player
     * @param enchantmentId The enchantment ID
     * @return Remaining seconds, or 0 if not on cooldown
     */
    public int getRemainingCooldown(Player player, String enchantmentId) {
        UUID uuid = player.getUniqueId();

        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(enchantmentId)) {
            return 0;
        }

        long cooldownEnd = playerCooldowns.get(enchantmentId);
        long remaining = cooldownEnd - System.currentTimeMillis();

        if (remaining <= 0) {
            playerCooldowns.remove(enchantmentId);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(uuid);
            }
            return 0;
        }

        return (int) Math.ceil(remaining / 1000.0);
    }

    /**
     * Remove all cooldowns for a player
     * @param player The player
     */
    public void removeCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    /**
     * Clear all cooldowns (usually on plugin disable)
     */
    public void cleanup() {
        cooldowns.clear();
    }

    /**
     * Get a formatted string showing remaining cooldown
     * @param player The player
     * @param enchantmentId The enchantment ID
     * @return Formatted cooldown string
     */
    public String getCooldownString(Player player, String enchantmentId) {
        int remaining = getRemainingCooldown(player, enchantmentId);

        if (remaining == 0) {
            return "Ready";
        }

        if (remaining < 60) {
            return remaining + "s";
        }

        int minutes = remaining / 60;
        int seconds = remaining % 60;

        if (seconds == 0) {
            return minutes + "m";
        }

        return minutes + "m " + seconds + "s";
    }

    /**
     * Get all active cooldowns for a player
     * @param player The player
     * @return Map of enchantment IDs to remaining seconds
     */
    public Map<String, Integer> getActiveCooldowns(Player player) {
        Map<String, Integer> active = new HashMap<>();
        UUID uuid = player.getUniqueId();

        if (!cooldowns.containsKey(uuid)) {
            return active;
        }

        Map<String, Long> playerCooldowns = new HashMap<>(cooldowns.get(uuid));
        for (Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
            int remaining = getRemainingCooldown(player, entry.getKey());
            if (remaining > 0) {
                active.put(entry.getKey(), remaining);
            }
        }

        return active;
    }

    /**
     * Remove a specific cooldown
     * @param player The player
     * @param enchantmentId The enchantment ID
     */
    public void removeCooldown(Player player, String enchantmentId) {
        UUID uuid = player.getUniqueId();

        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).remove(enchantmentId);
            if (cooldowns.get(uuid).isEmpty()) {
                cooldowns.remove(uuid);
            }
        }
    }

    /**
     * Check if player has any active cooldowns
     * @param player The player
     * @return true if player has at least one active cooldown
     */
    public boolean hasAnyCooldowns(Player player) {
        return !getActiveCooldowns(player).isEmpty();
    }
}