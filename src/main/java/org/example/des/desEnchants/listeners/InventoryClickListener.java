package org.example.des.desEnchants.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.DustType;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;
import org.example.des.desEnchants.core.utils.EnchantmentUtils;
import org.example.des.desEnchants.gui.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private final DesEnchants plugin;
    private final Random random = new Random();
    private final Map<UUID, AnvilGUI> anvilGUIs = new HashMap<>();

    public InventoryClickListener(DesEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String guiType = plugin.getGuiManager().getOpenGUI(player);

        if (guiType == null) return;

        // Special handling for anvil GUI
        if (guiType.equals("anvil")) {
            handleAnvilGUI(event, player);
            return;
        }

        // Cancel the event for other GUIs
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Play click sound
        if (plugin.getConfig().getBoolean("gui.enable-sounds", true)) {
            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.click-item", "UI_BUTTON_CLICK"));
                player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
            } catch (IllegalArgumentException ignored) {}
        }

        // Handle based on GUI type
        if (guiType.startsWith("equipment-")) {
            handleEquipmentGUIClick(player, event.getSlot(), guiType);
        } else {
            switch (guiType) {
                case "main" -> handleMainGUIClick(player, event.getSlot(), clicked);
                case "armor", "weapons", "tools" -> handleCategoryGUIClick(player, event.getSlot(), clicked, guiType);
                case "confirmation" -> handleConfirmationClick(player, event.getSlot());
            }
        }
    }

    private void handleMainGUIClick(Player player, int slot, ItemStack clicked) {
        switch (slot) {
            case 11 -> {
                // Armor category
                player.closeInventory();
                new CategoryGUI(plugin, player, "armor").open();
            }
            case 13 -> {
                // Weapons category
                player.closeInventory();
                new CategoryGUI(plugin, player, "weapons").open();
            }
            case 15 -> {
                // Tools category
                player.closeInventory();
                new CategoryGUI(plugin, player, "tools").open();
            }
            case 19 -> {
                // Angel Dust purchase
                handleDustPurchase(player, DustType.ANGEL_DUST);
            }
            case 22 -> {
                // Anvil
                player.closeInventory();
                AnvilGUI anvilGUI = new AnvilGUI(plugin, player);
                anvilGUIs.put(player.getUniqueId(), anvilGUI);
                anvilGUI.open();
            }
            case 25 -> {
                // Demon Dust purchase
                handleDustPurchase(player, DustType.DEMON_DUST);
            }
            case 29 -> {
                // Common enchantment purchase
                handlePurchase(player, EnchantmentRarity.COMMON);
            }
            case 31 -> {
                // Rare enchantment purchase
                handlePurchase(player, EnchantmentRarity.RARE);
            }
            case 33 -> {
                // Legendary enchantment purchase
                handlePurchase(player, EnchantmentRarity.LEGENDARY);
            }
        }
    }

    private void handleCategoryGUIClick(Player player, int slot, ItemStack clicked, String category) {
        // Back button
        if (slot == 31) {
            player.closeInventory();
            new MainEnchantmentGUI(plugin, player).open();
            return;
        }

        // Get the target from the clicked slot
        CategoryGUI categoryGUI = new CategoryGUI(plugin, player, category);
        EnchantmentTarget target = categoryGUI.getTargetFromSlot(slot);

        if (target != null) {
            player.closeInventory();
            new EquipmentGUI(plugin, player, target).open();
        }
    }

    private void handleEquipmentGUIClick(Player player, int slot, String guiType) {
        if (slot == 49) { // Back button
            player.closeInventory();
            String targetName = guiType.substring(10); // Remove "equipment-" prefix
            String category = getCategoryFromTarget(targetName);
            new CategoryGUI(plugin, player, category).open();
        }
    }

    private void handleConfirmationClick(Player player, int slot) {
        if (slot == 11 || slot == 15) {
            player.closeInventory();
        }
    }

    private void handleAnvilGUI(InventoryClickEvent event, Player player) {
        AnvilGUI anvilGUI = anvilGUIs.get(player.getUniqueId());
        if (anvilGUI == null) return;

        int slot = event.getSlot();

        // Allow interaction with input slots
        if (slot == 11 || slot == 15) {
            // Let the event happen normally
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                anvilGUI.updateCombineButton();
            }, 1L);
            return;
        }

        // Cancel other clicks
        event.setCancelled(true);

        // Handle other slots
        anvilGUI.handleClick(slot, event.getCursor());
    }

    private void handlePurchase(Player player, EnchantmentRarity rarity) {
        // Check if player has enough levels
        if (player.getLevel() < rarity.getXpCost()) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.not-enough-levels",
                    rarity.getXpCost(), rarity.getDisplayName());

            // Play fail sound
            if (plugin.getConfig().getBoolean("gui.enable-sounds", true)) {
                try {
                    Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.purchase-fail", "ENTITY_VILLAGER_NO"));
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                } catch (IllegalArgumentException ignored) {}
            }
            return;
        }

        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.inventory-full");
            return;
        }

        // Get random enchantment
        CustomEnchantment enchantment = plugin.getEnchantmentManager().getRandomEnchantment(rarity);
        if (enchantment == null) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.no-enchants-available");
            return;
        }

        // Take levels
        player.setLevel(player.getLevel() - rarity.getXpCost());

        // Create enchanted book
        int level = 1; // You can add random level logic later
        ItemStack book = EnchantmentUtils.createEnchantedBook(plugin, enchantment, level);
        player.getInventory().addItem(book);

        // Send success message
        plugin.getLanguageManager().sendMessage(player, "gui.purchase.success",
                enchantment.getRarity().format(enchantment.getDisplayName() + " " + level));

        // Play success sound
        if (plugin.getConfig().getBoolean("gui.enable-sounds", true)) {
            try {
                Sound sound = Sound.valueOf(plugin.getConfig().getString("gui.sounds.purchase-success", "ENTITY_PLAYER_LEVELUP"));
                player.playSound(player.getLocation(), sound, 1.0f, 1.5f);
            } catch (IllegalArgumentException ignored) {}
        }

        // Close inventory
        player.closeInventory();
    }

    private void handleDustPurchase(Player player, DustType dustType) {
        // Check if player has enough levels
        if (player.getLevel() < dustType.getXpCost()) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.not-enough-levels",
                    dustType.getXpCost(), dustType.getDisplayName());

            // Play fail sound
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            plugin.getLanguageManager().sendMessage(player, "gui.purchase.inventory-full");
            return;
        }

        // Take levels
        player.setLevel(player.getLevel() - dustType.getXpCost());

        // Generate random percentage
        int percentage = random.nextInt(dustType.getMaxBonus() - dustType.getMinBonus() + 1) + dustType.getMinBonus();

        // Create dust item
        ItemStack dust = EnchantmentUtils.createDust(plugin, dustType, percentage);
        player.getInventory().addItem(dust);

        // Send success message
        plugin.getLanguageManager().sendMessage(player, "gui.purchase.success",
                dustType.format(dustType.getDisplayName() + " +" + percentage + "%"));

        // Play success sound
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

        // Close inventory
        player.closeInventory();
    }

    private String getCategoryFromTarget(String targetName) {
        try {
            EnchantmentTarget target = EnchantmentTarget.valueOf(targetName);
            return switch (target) {
                case HELMET, CHESTPLATE, LEGGINGS, BOOTS, ALL_ARMOR -> "armor";
                case SWORD, AXE, BOW, TRIDENT, ALL_WEAPONS -> "weapons";
                case PICKAXE, SHOVEL, HOE, ALL_TOOLS -> "tools";
            };
        } catch (IllegalArgumentException e) {
            return "main";
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        plugin.getGuiManager().removeOpenGUI(player);

        // Clean up anvil GUI
        anvilGUIs.remove(player.getUniqueId());
    }
}