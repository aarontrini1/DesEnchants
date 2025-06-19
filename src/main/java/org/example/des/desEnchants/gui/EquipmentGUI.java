package org.example.des.desEnchants.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;
import org.example.des.desEnchants.core.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class EquipmentGUI {

    private final DesEnchants plugin;
    private final Player player;
    private final EnchantmentTarget target;
    private Inventory inventory;

    public EquipmentGUI(DesEnchants plugin, Player player, EnchantmentTarget target) {
        this.plugin = plugin;
        this.player = player;
        this.target = target;
        setupGUI();
    }

    private void setupGUI() {
        String title = ChatColor.translateAlternateColorCodes('&',
                "&5&l" + target.getDisplayName() + " Enchantments");
        inventory = Bukkit.createInventory(null, 54, title);

        // Fill border
        ItemStack glass = ItemBuilder.quick(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glass);
            inventory.setItem(45 + i, glass);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, glass);
            inventory.setItem(i + 8, glass);
        }

        // Add enchantments
        List<CustomEnchantment> enchantments = getEnchantmentsForTarget();

        int slot = 10;
        for (CustomEnchantment enchant : enchantments) {
            if (slot >= 44) break;

            // Skip border slots
            if (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
                continue;
            }

            inventory.setItem(slot, createEnchantmentDisplay(enchant));
            slot++;
        }

        // Back button
        inventory.setItem(49, ItemBuilder.quick(Material.ARROW, "&cBack"));
    }

    private List<CustomEnchantment> getEnchantmentsForTarget() {
        List<CustomEnchantment> result = new ArrayList<>();

        for (CustomEnchantment enchant : plugin.getEnchantmentManager().getAllEnchantments()) {
            if (enchant.getTarget() == target ||
                    (target == EnchantmentTarget.HELMET && enchant.getTarget() == EnchantmentTarget.ALL_ARMOR) ||
                    (target == EnchantmentTarget.SWORD && enchant.getTarget() == EnchantmentTarget.ALL_WEAPONS)) {
                result.add(enchant);
            }
        }

        return result;
    }

    private ItemStack createEnchantmentDisplay(CustomEnchantment enchant) {
        Material material = switch (target) {
            case HELMET -> Material.DIAMOND_HELMET;
            case CHESTPLATE -> Material.DIAMOND_CHESTPLATE;
            case LEGGINGS -> Material.DIAMOND_LEGGINGS;
            case BOOTS -> Material.DIAMOND_BOOTS;
            case SWORD -> Material.DIAMOND_SWORD;
            case AXE -> Material.DIAMOND_AXE;
            case BOW -> Material.BOW;
            case PICKAXE -> Material.DIAMOND_PICKAXE;
            case SHOVEL -> Material.DIAMOND_SHOVEL;
            case HOE -> Material.DIAMOND_HOE;
            default -> Material.ENCHANTED_BOOK;
        };

        ItemBuilder builder = new ItemBuilder(material)
                .name(enchant.getRarity().format(enchant.getDisplayName()))
                .lore("");

        if (enchant.getDescription() != null) {
            for (String line : enchant.getDescription()) {
                builder.addLoreLine(line);
            }
            builder.addLoreLine("");
        }

        builder.addLoreLine("&eMax Level: &f" + enchant.getMaxLevel());
        builder.addLoreLine("&eRarity: " + enchant.getRarity().format(enchant.getRarity().getDisplayName()));
        builder.addLoreLine("&eSuccess Rate: &a" + enchant.getSuccessRate() + "%");
        builder.addLoreLine("&eDestroy Rate: &c" + enchant.getDestroyRate() + "%");

        return builder.build();
    }

    public void open() {
        plugin.getGuiManager().setOpenGUI(player, "equipment-" + target.name());
        player.openInventory(inventory);
    }
}