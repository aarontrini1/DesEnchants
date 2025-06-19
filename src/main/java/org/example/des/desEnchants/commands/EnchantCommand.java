package org.example.des.desEnchants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.gui.MainEnchantmentGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final DesEnchants plugin;

    public EnchantCommand(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("de") && sender.hasPermission("desenchants.use")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.prefix + "This command can only be used by players!");
                    return true;
                }

                Player player = (Player) sender;
                // Open main GUI
                plugin.getGuiManager().setOpenGUI(player, "main");
                new MainEnchantmentGUI(plugin, player).open();
                return true;
            }

            // Handle subcommands
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "help":
                    sendHelp(sender);
                    break;

                case "reload":
                    if (!sender.hasPermission("desenchants.reload")) {
                        plugin.getLanguageManager().sendMessage(sender, "no-permission");
                        return true;
                    }
                    plugin.reload();
                    plugin.getLanguageManager().sendMessage(sender, "reload-success");
                    break;

                case "list":
                    if (!sender.hasPermission("desenchants.list")) {
                        plugin.getLanguageManager().sendMessage(sender, "no-permission");
                        return true;
                    }
                    // TODO: Implement list command
                    sender.sendMessage(plugin.prefix + "Available enchantments: " +
                            plugin.getEnchantmentManager().getRegisteredCount());
                    break;

                default:
                    plugin.getLanguageManager().sendMessage(sender, "unknown-command");
                    break;
            }

            return true;
        }

        // If no args, open GUI (player only)
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "reload", "list", "give", "info");
            String partial = args[0].toLowerCase();

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    completions.add(subCommand);
                }
            }
        }

        return completions;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.prefix + "§6DesEnchants Commands:");
        sender.sendMessage("§e/de §7- Open the enchantments GUI");
        sender.sendMessage("§e/de help §7- Show this help message");

        if (sender.hasPermission("desenchants.list")) {
            sender.sendMessage("§e/de list §7- List all enchantments");
        }
        if (sender.hasPermission("desenchants.reload")) {
            sender.sendMessage("§e/de reload §7- Reload the plugin");
        }
    }
}