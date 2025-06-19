package org.example.des.desEnchants.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.example.des.desEnchants.DesEnchants;

import java.util.List;
import java.util.stream.Collectors;

public class LanguageManager {

    private final DesEnchants plugin;
    private FileConfiguration messagesConfig;

    public LanguageManager(DesEnchants plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        ConfigManager configManager = plugin.getConfigManager();
        if (configManager != null) {
            this.messagesConfig = configManager.getConfig("messages.yml");
        }
    }

    public String getMessage(String path) {
        if (messagesConfig == null) {
            return ChatColor.RED + "Messages not loaded!";
        }

        String message = messagesConfig.getString(path);
        if (message == null) {
            plugin.getLogger().warning("Missing message: " + path);
            return ChatColor.RED + "Message not found: " + path;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path, Object... replacements) {
        String message = getMessage(path);

        // Replace placeholders {0}, {1}, etc.
        for (int i = 0; i < replacements.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(replacements[i]));
        }

        return message;
    }

    public List<String> getMessageList(String path) {
        if (messagesConfig == null) {
            return List.of(ChatColor.RED + "Messages not loaded!");
        }

        return messagesConfig.getStringList(path).stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(plugin.prefix + getMessage(path));
    }

    public void sendMessage(CommandSender sender, String path, Object... replacements) {
        sender.sendMessage(plugin.prefix + getMessage(path, replacements));
    }

    public void sendRawMessage(CommandSender sender, String path) {
        sender.sendMessage(getMessage(path));
    }

    public void sendRawMessage(CommandSender sender, String path, Object... replacements) {
        sender.sendMessage(getMessage(path, replacements));
    }
}