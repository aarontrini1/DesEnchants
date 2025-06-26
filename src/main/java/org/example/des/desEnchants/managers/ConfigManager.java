package org.example.des.desEnchants.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.example.des.desEnchants.DesEnchants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final DesEnchants plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> configFiles = new HashMap<>();

    // Config options
    private boolean debugMode;
    private int maxEnchantments;
    private boolean useVanillaEnchantments;
    private boolean enchantmentGlow;

    public ConfigManager(DesEnchants plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Load main config
        loadConfig("config.yml");

        // Load other configs
        loadConfig("enchantments.yml");
        loadConfig("messages.yml");
        loadConfig("guis.yml");

        // Load values
        loadConfigValues();
    }

    private void loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(fileName, config);
        configFiles.put(fileName, file);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig("config.yml");

        debugMode = config.getBoolean("debug-mode", false);
        maxEnchantments = config.getInt("max-enchantments", 5);
        useVanillaEnchantments = config.getBoolean("use-vanilla-enchantments", true);
        enchantmentGlow = config.getBoolean("enchantment-glow", true);
    }

    public void reloadConfigs() {
        configs.clear();
        configFiles.clear();
        loadConfigs();
    }

    public void saveConfig(String fileName) {
        FileConfiguration config = configs.get(fileName);
        File file = configFiles.get(fileName);

        if (config != null && file != null) {
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save config: " + fileName);
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig(String fileName) {
        return configs.get(fileName);
    }

    // Getters for config values
    public boolean isDebugMode() {
        return debugMode;
    }

    public int getMaxEnchantments() {
        return maxEnchantments;
    }

    public boolean useVanillaEnchantments() {
        return useVanillaEnchantments;
    }

    public boolean isEnchantmentGlow() {
        return enchantmentGlow;
    }
}