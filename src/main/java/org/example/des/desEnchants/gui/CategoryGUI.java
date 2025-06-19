package org.example.des.desEnchants.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;
import org.example.des.desEnchants.core.utils.ItemBuilder;

public class CategoryGUI {

    private final DesEnchants plugin;
    private final Player player;
    private final String category;
    private Inventory inventory;

    public CategoryGUI(DesEnchants plugin, Player player, String category) {
        this.plugin = plugin;
        this.player = player;
        this.category = category;
        setupGUI();
    }

    private void setupGUI() {
        // Get title based on category
        String title = switch (category) {
            case "armor" -> plugin.getConfig().getString("gui.armor-title", "&b&lArmor Enchantments");
            case "weapons" -> plugin.getConfig().getString("gui.weapons-title", "&c&lWeapon Enchantments");
            case "tools" -> plugin.getConfig().getString("gui.tools-title", "&6&lTool Enchantments");
            default -> "&5&lEnchantments";
        };

        title = ChatColor.translateAlternateColorCodes('&', title);
        inventory = Bukkit.createInventory(null, 36, title);

        // Fill with glass
        ItemStack glass = ItemBuilder.quick(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, glass);
        }

        // Add equipment pieces based on category
        switch (category) {
            case "armor" -> {
                inventory.setItem(10, createEquipmentItem(Material.DIAMOND_HELMET, "&bHelmet Enchantments", EnchantmentTarget.HELMET));
                inventory.setItem(12, createEquipmentItem(Material.DIAMOND_CHESTPLATE, "&bChestplate Enchantments", EnchantmentTarget.CHESTPLATE));
                inventory.setItem(14, createEquipmentItem(Material.DIAMOND_LEGGINGS, "&bLeggings Enchantments", EnchantmentTarget.LEGGINGS));
                inventory.setItem(16, createEquipmentItem(Material.DIAMOND_BOOTS, "&bBoots Enchantments", EnchantmentTarget.BOOTS));
            }
            case "weapons" -> {
                inventory.setItem(11, createEquipmentItem(Material.DIAMOND_SWORD, "&cSword Enchantments", EnchantmentTarget.SWORD));
                inventory.setItem(13, createEquipmentItem(Material.DIAMOND_AXE, "&cAxe Enchantments", EnchantmentTarget.AXE));
                inventory.setItem(15, createEquipmentItem(Material.BOW, "&cBow Enchantments", EnchantmentTarget.BOW));
            }
            case "tools" -> {
                inventory.setItem(10, createEquipmentItem(Material.DIAMOND_PICKAXE, "&6Pickaxe Enchantments", EnchantmentTarget.PICKAXE));
                inventory.setItem(12, createEquipmentItem(Material.DIAMOND_AXE, "&6Axe Enchantments", EnchantmentTarget.AXE));
                inventory.setItem(14, createEquipmentItem(Material.DIAMOND_SHOVEL, "&6Shovel Enchantments", EnchantmentTarget.SHOVEL));
                inventory.setItem(16, createEquipmentItem(Material.DIAMOND_HOE, "&6Hoe Enchantments", EnchantmentTarget.HOE));
            }
        }

        // Back button
        inventory.setItem(31, ItemBuilder.quick(Material.ARROW, "&cBack to Main Menu"));
    }

    private ItemStack createEquipmentItem(Material material, String name, EnchantmentTarget target) {
        return new ItemBuilder(material)
                .name(name)
                .lore(
                        "",
                        "&7View all enchantments",
                        "&7available for this item.",
                        "",
                        "&eClick to view!"
                )
                .hideAttributes()
                .build();
    }

    public void open() {
        plugin.getGuiManager().setOpenGUI(player, category);
        player.openInventory(inventory);
    }

    public EnchantmentTarget getTargetFromSlot(int slot) {
        return switch (category) {
            case "armor" -> switch (slot) {
                case 10 -> EnchantmentTarget.HELMET;
                case 12 -> EnchantmentTarget.CHESTPLATE;
                case 14 -> EnchantmentTarget.LEGGINGS;
                case 16 -> EnchantmentTarget.BOOTS;
                default -> null;
            };
            case "weapons" -> switch (slot) {
                case 11 -> EnchantmentTarget.SWORD;
                case 13 -> EnchantmentTarget.AXE;
                case 15 -> EnchantmentTarget.BOW;
                default -> null;
            };
            case "tools" -> switch (slot) {
                case 10 -> EnchantmentTarget.PICKAXE;
                case 12 -> EnchantmentTarget.AXE;
                case 14 -> EnchantmentTarget.SHOVEL;
                case 16 -> EnchantmentTarget.HOE;
                default -> null;
            };
            default -> null;
        };
    }
}