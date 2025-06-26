package org.example.des.desEnchants;

import org.bukkit.plugin.java.JavaPlugin;
import org.example.des.desEnchants.managers.*;
import org.example.des.desEnchants.listeners.*;
import org.example.des.desEnchants.commands.EnchantCommand;

public class DesEnchants extends JavaPlugin {

    private static DesEnchants instance;

    // Managers
    private ConfigManager configManager;
    private EnchantmentManager enchantmentManager;
    private GUIManager guiManager;
    private LanguageManager languageManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers in correct order
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);
        this.enchantmentManager = new EnchantmentManager(this);
        this.guiManager = new GUIManager(this);
        this.cooldownManager = new CooldownManager(this);

        // Load configurations
        configManager.loadConfigs();

        // Load enchantments
        enchantmentManager.loadEnchantments();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        getLogger().info("DesEnchants v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save cooldowns
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
        }

        // Clear enchantments
        if (enchantmentManager != null) {
            enchantmentManager.unloadEnchantments();
        }

        getLogger().info("DesEnchants has been disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EnchantmentListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
    }

    private void registerCommands() {
        getCommand("desenchants").setExecutor(new EnchantCommand(this));
    }

    public void reload() {
        // Reload configs
        configManager.reloadConfigs();

        // Reload enchantments
        enchantmentManager.reload();

        getLogger().info("DesEnchants has been reloaded!");
    }

    // Getters
    public static DesEnchants getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
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