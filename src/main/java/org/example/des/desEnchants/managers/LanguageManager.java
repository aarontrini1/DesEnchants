package org.example.des.desEnchants.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.example.des.desEnchants.DesEnchants;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final DesEnchants plugin;
    private final Map<String, String> messages = new HashMap<>();

    public LanguageManager(DesEnchants plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        FileConfiguration config = plugin.getConfigManager().getConfig("messages.yml");

        if (config == null) {
            plugin.getLogger().warning("Could not load messages.yml!");
            loadDefaultMessages();
            return;
        }

        // Load all messages from config
        for (String key : config.getKeys(false)) {
            messages.put(key, config.getString(key));
        }

        // Ensure all default messages exist
        loadDefaultMessages();
    }

    private void loadDefaultMessages() {
        // Default messages if not in config
        addDefault("prefix", "&8[&6DesEnchants&8] ");
        addDefault("no-permission", "&cYou don't have permission to do that!");
        addDefault("player-only", "&cThis command can only be used by players!");
        addDefault("invalid-command", "&cInvalid command! Use /desenchants help");
        addDefault("reload-success", "&aPlugin reloaded successfully!");

        // Enchantment messages
        addDefault("enchantment-success", "&aSuccessfully applied %enchantment%!");
        addDefault("enchantment-failed", "&cThe enchantment failed to apply!");
        addDefault("enchantment-destroyed", "&cThe enchantment destroyed your item!");
        addDefault("cannot-enchant-item", "&cThis enchantment cannot be applied to this item!");
        addDefault("max-enchantments-reached", "&cThis item has reached the maximum number of enchantments!");

        // Phoenix enchantment
        addDefault("phoenix-activated", "&6&lPHOENIX! &eYou have been revived!");

        // Cooldown messages
        addDefault("enchantment-cooldown", "&cThis enchantment is on cooldown for %time% seconds!");
    }

    private void addDefault(String key, String value) {
        messages.putIfAbsent(key, value);
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                messages.getOrDefault("prefix", "") + messages.getOrDefault(key, "&cMissing message: " + key));
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);

        // Replace placeholders
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        return message;
    }

    public void sendMessage(Player player, String key) {
        player.sendMessage(getMessage(key));
    }

    public void sendMessage(Player player, String key, String... replacements) {
        player.sendMessage(getMessage(key, replacements));
    }

    public void reload() {
        messages.clear();
        loadMessages();
    }
}