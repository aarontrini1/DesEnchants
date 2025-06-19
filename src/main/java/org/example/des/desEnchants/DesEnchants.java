package org.example.des.desEnchants;
import org.example.des.desEnchants.commands.EnchantCommand;
import org.example.des.desEnchants.listeners.*;
import org.example.des.desEnchants.managers.ConfigManager;
import org.example.des.desEnchants.managers.EnchantmentManager;
import org.example.des.desEnchants.managers.GUIManager;
import org.example.des.desEnchants.managers.LanguageManager;
import org.example.des.desEnchants.shared.cooldown.CooldownManager;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class DesEnchants extends JavaPlugin {

    private static DesEnchants instance;
    public final String prefix = ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Ench" + ChatColor.GREEN + "ants" + ChatColor.WHITE + "]" + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE;

    // Managers
    private EnchantmentManager enchantmentManager;
    private ConfigManager configManager;
    private GUIManager guiManager;
    private LanguageManager languageManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        // Plugin startup logic
        createDefaultConfig();
        saveDefaultResources();

        // Initialize managers
        initializeManagers();

        // Register enchantments
        registerEnchantments();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        getLogger().info(prefix + "DesEnchants v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info(prefix + "Loaded " + enchantmentManager.getRegisteredCount() + " custom enchantments!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (cooldownManager != null) {
            cooldownManager.cleanup();
        }

        getLogger().info(prefix + "DesEnchants plugin disabled");
    }

    private void createDefaultConfig() {
        // Get the plugin's data folder (DesEnchants folder)
        File dataFolder = getDataFolder();

        // Ensure the DesEnchants folder exists
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Create config.yml if it doesn't exist
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Config file not found in DesEnchants folder; creating default config.yml...");
            saveResource("config.yml", false);
        }
    }

    private void saveDefaultResources() {
        // Create enchantments folder
        File enchantmentsFolder = new File(getDataFolder(), "enchantments");
        if (!enchantmentsFolder.exists()) {
            enchantmentsFolder.mkdir();
        }

        // Save default enchantment configs
        saveResource("messages.yml", false);
        saveResource("enchantments/armor.yml", false);
        saveResource("enchantments/weapons.yml", false);
        saveResource("enchantments/tools.yml", false);
    }

    private void initializeManagers() {
        // Initialize in correct order
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        languageManager.reload();
        enchantmentManager = new EnchantmentManager(this);
        guiManager = new GUIManager(this);
        cooldownManager = new CooldownManager(this);
    }

    private void registerEnchantments() {
        // This will be implemented when we create the EnchantmentManager
        enchantmentManager.loadEnchantments();
    }

    private void registerListeners() {
        // Register event listeners
         getServer().getPluginManager().registerEvents(new EnchantmentListener(this), this);
         getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
         getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
         getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
         getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
         getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
    }

    private void registerCommands() {
        // Register the main command
//        getCommand("de").setExecutor(new EnchantCommand(this));
        Objects.requireNonNull(this.getCommand("de")).setExecutor(new EnchantCommand(this));
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        languageManager.reload();
        enchantmentManager.reload();

        getLogger().info(prefix + "Configuration reloaded!");
    }

    // Getters
    public static DesEnchants getInstance() {
        return instance;
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}