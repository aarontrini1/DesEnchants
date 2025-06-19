package org.example.des.desEnchants.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.example.des.desEnchants.DesEnchants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {

    private final DesEnchants plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> configFiles = new HashMap<>();

    public ConfigManager(DesEnchants plugin) {
        this.plugin = plugin;
        loadAllConfigs();
    }

    private void loadAllConfigs() {
        // Ensure directories exist
        createDirectories();

        // Load all configuration files
        loadConfig("messages.yml");
        loadConfig("enchantments/armor.yml");
        loadConfig("enchantments/weapons.yml");
        loadConfig("enchantments/tools.yml");
    }

    private void createDirectories() {
        File enchantmentsDir = new File(plugin.getDataFolder(), "enchantments");
        if (!enchantmentsDir.exists()) {
            enchantmentsDir.mkdirs();
        }
    }

    private void loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        // Save default if doesn't exist
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Could not save default config: " + fileName, e);
                return;
            }
        }

        // Load the configuration
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(fileName, config);
        configFiles.put(fileName, file);
    }

    public FileConfiguration getConfig(String fileName) {
        return configs.get(fileName);
    }

    public void saveConfig(String fileName) {
        FileConfiguration config = configs.get(fileName);
        File file = configFiles.get(fileName);

        if (config != null && file != null) {
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config file: " + fileName, e);
            }
        }
    }

    public void saveAll() {
        for (String fileName : configs.keySet()) {
            saveConfig(fileName);
        }
    }

    public void reload() {
        // Clear and reload all configs
        configs.clear();
        configFiles.clear();
        plugin.reloadConfig();
        loadAllConfigs();
    }

    // Main config convenience methods
    public FileConfiguration getMainConfig() {
        return plugin.getConfig();
    }

    public boolean isDebugMode() {
        return getMainConfig().getBoolean("settings.debug", false);
    }

    public int getMaxEnchantmentsPerItem() {
        return getMainConfig().getInt("settings.max-enchantments-per-item", 5);
    }

    public boolean showEnchantmentDescriptions() {
        return getMainConfig().getBoolean("settings.show-descriptions", true);
    }
}