package org.example.des.desEnchants.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.example.des.desEnchants.DesEnchants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final DesEnchants plugin;
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();
    private final File cooldownFile;

    public CooldownManager(DesEnchants plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "cooldowns.yml");
        loadCooldowns();
    }

    public void setCooldown(Player player, String enchantment, long duration) {
        cooldowns.computeIfAbsent(enchantment, k -> new HashMap<>())
                .put(player.getUniqueId(), System.currentTimeMillis() + duration);
    }

    public boolean isOnCooldown(Player player, String enchantment) {
        Map<UUID, Long> enchantCooldowns = cooldowns.get(enchantment);
        if (enchantCooldowns == null) return false;

        Long endTime = enchantCooldowns.get(player.getUniqueId());
        if (endTime == null) return false;

        if (System.currentTimeMillis() >= endTime) {
            enchantCooldowns.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    public long getCooldownRemaining(Player player, String enchantment) {
        Map<UUID, Long> enchantCooldowns = cooldowns.get(enchantment);
        if (enchantCooldowns == null) return 0;

        Long endTime = enchantCooldowns.get(player.getUniqueId());
        if (endTime == null) return 0;

        long remaining = endTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public void removeCooldown(Player player, String enchantment) {
        Map<UUID, Long> enchantCooldowns = cooldowns.get(enchantment);
        if (enchantCooldowns != null) {
            enchantCooldowns.remove(player.getUniqueId());
        }
    }

    public void clearCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        cooldowns.values().forEach(map -> map.remove(uuid));
    }

    public void clearAllCooldowns() {
        cooldowns.clear();
    }

    public void saveCooldowns() {
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, Map<UUID, Long>> enchantEntry : cooldowns.entrySet()) {
            String enchantment = enchantEntry.getKey();

            for (Map.Entry<UUID, Long> playerEntry : enchantEntry.getValue().entrySet()) {
                String path = enchantment + "." + playerEntry.getKey().toString();
                config.set(path, playerEntry.getValue());
            }
        }

        try {
            config.save(cooldownFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save cooldowns!");
            e.printStackTrace();
        }
    }

    private void loadCooldowns() {
        if (!cooldownFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(cooldownFile);
        long currentTime = System.currentTimeMillis();

        for (String enchantment : config.getKeys(false)) {
            Map<UUID, Long> enchantCooldowns = new HashMap<>();

            for (String uuidString : config.getConfigurationSection(enchantment).getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    long endTime = config.getLong(enchantment + "." + uuidString);

                    // Only load if cooldown hasn't expired
                    if (endTime > currentTime) {
                        enchantCooldowns.put(uuid, endTime);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in cooldowns.yml: " + uuidString);
                }
            }

            if (!enchantCooldowns.isEmpty()) {
                cooldowns.put(enchantment, enchantCooldowns);
            }
        }
    }
}