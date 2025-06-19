package org.example.des.desEnchants.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;

import java.util.Arrays;
import java.util.function.Consumer;

public class ConfirmationGUI {

    private final DesEnchants plugin;
    private final Player player;
    private final String title;
    private final String question;
    private final Consumer<Boolean> callback;
    private Inventory inventory;

    public ConfirmationGUI(DesEnchants plugin, Player player, String title, String question, Consumer<Boolean> callback) {
        this.plugin = plugin;
        this.player = player;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.question = question;
        this.callback = callback;
        setupGUI();
    }

    private void setupGUI() {
        inventory = Bukkit.createInventory(null, 27, title);

        // Fill with black glass
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, blackGlass);
        }

        // Question item in middle
        ItemStack questionItem = createItem(Material.PAPER, "&e" + question);
        inventory.setItem(13, questionItem);

        // Confirm button
        ItemStack confirm = createItem(Material.LIME_WOOL, "&a&lCONFIRM");
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Click to confirm",
                ChatColor.GRAY + "this action."
        ));
        confirm.setItemMeta(confirmMeta);
        inventory.setItem(11, confirm);

        // Cancel button
        ItemStack cancel = createItem(Material.RED_WOOL, "&c&lCANCEL");
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Click to cancel",
                ChatColor.GRAY + "this action."
        ));
        cancel.setItemMeta(cancelMeta);
        inventory.setItem(15, cancel);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        plugin.getGuiManager().setOpenGUI(player, "confirmation");
        player.openInventory(inventory);
    }

    public void handleClick(int slot) {
        if (slot == 11) {
            // Confirm
            player.closeInventory();
            if (callback != null) {
                callback.accept(true);
            }
        } else if (slot == 15) {
            // Cancel
            player.closeInventory();
            if (callback != null) {
                callback.accept(false);
            }
        }
    }

    public Consumer<Boolean> getCallback() {
        return callback;
    }
}