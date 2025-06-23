package org.example.des.desEnchants.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.DustType;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainEnchantmentGUI {

    private final DesEnchants plugin;
    private final Player player;
    private Inventory inventory;

    public MainEnchantmentGUI(DesEnchants plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        setupGUI();
    }

    private void setupGUI() {
        // Create inventory
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("gui.main-title", "&5&l✦ Enchantments ✦"));
        inventory = Bukkit.createInventory(null, 45, title);

        // Fill with glass panes
        fillBorder();

        // Add category items
        addCategoryItems();

        // Add purchase items
        addPurchaseItems();

        // Add info item
        addInfoItem();
    }

    private void fillBorder() {
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        // Fill border
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, blackGlass);
            inventory.setItem(36 + i, blackGlass);
        }
        for (int i = 9; i < 36; i += 9) {
            inventory.setItem(i, blackGlass);
            inventory.setItem(i + 8, blackGlass);
        }
    }

    private void addCategoryItems() {
        // Armor category
        ItemStack armor = createItem(Material.DIAMOND_CHESTPLATE,
                plugin.getConfig().getString("gui.armor-title", "&b&lArmor Enchantments"));
        ItemMeta armorMeta = armor.getItemMeta();
        List<String> armorLore = new ArrayList<>();
        armorLore.add(ChatColor.GRAY + "Enchantments for all armor pieces");
        armorLore.add("");
        armorLore.add(ChatColor.translateAlternateColorCodes('&',
                plugin.getLanguageManager().getMessage("gui.item-lore.click-to-open")));
        armorMeta.setLore(armorLore);
        armor.setItemMeta(armorMeta);
        inventory.setItem(11, armor);

        // Weapons category
        ItemStack weapons = createItem(Material.DIAMOND_SWORD,
                plugin.getConfig().getString("gui.weapons-title", "&c&lWeapon Enchantments"));
        ItemMeta weaponsMeta = weapons.getItemMeta();
        List<String> weaponsLore = new ArrayList<>();
        weaponsLore.add(ChatColor.GRAY + "Enchantments for weapons");
        weaponsLore.add("");
        weaponsLore.add(ChatColor.translateAlternateColorCodes('&',
                plugin.getLanguageManager().getMessage("gui.item-lore.click-to-open")));
        weaponsMeta.setLore(weaponsLore);
        weapons.setItemMeta(weaponsMeta);
        inventory.setItem(13, weapons);

        // Tools category
        ItemStack tools = createItem(Material.DIAMOND_PICKAXE,
                plugin.getConfig().getString("gui.tools-title", "&6&lTool Enchantments"));
        ItemMeta toolsMeta = tools.getItemMeta();
        List<String> toolsLore = new ArrayList<>();
        toolsLore.add(ChatColor.GRAY + "Enchantments for tools");
        toolsLore.add("");
        toolsLore.add(ChatColor.translateAlternateColorCodes('&',
                plugin.getLanguageManager().getMessage("gui.item-lore.click-to-open")));
        toolsMeta.setLore(toolsLore);
        tools.setItemMeta(toolsMeta);
        inventory.setItem(15, tools);
    }

    private void addPurchaseItems() {
        // Common enchantment book
        inventory.setItem(29, createPurchaseItem(EnchantmentRarity.COMMON));

        // Rare enchantment book
        inventory.setItem(31, createPurchaseItem(EnchantmentRarity.RARE));

        // Legendary enchantment book
        inventory.setItem(33, createPurchaseItem(EnchantmentRarity.LEGENDARY));

        // Angel Dust
        inventory.setItem(19, createDustItem(DustType.ANGEL_DUST));

        // Demon Dust
        inventory.setItem(25, createDustItem(DustType.DEMON_DUST));

        // Anvil
        inventory.setItem(22, createAnvilItem());
    }

    private ItemStack createDustItem(DustType dustType) {
        ItemBuilder builder = new ItemBuilder(dustType.getMaterial())
                .name(dustType.format("✦ " + dustType.getDisplayName() + " ✦"))
                .lore(
                        "&7" + dustType.getDescription(),
                        "",
                        "&7Bonus: &a" + dustType.getMinBonus() + "-" + dustType.getMaxBonus() + "%",
                        "",
                        "&7Cost: &a" + dustType.getXpCost() + " Levels", // Fixed this line
                        "",
                        "&eClick to purchase!"
                )
                .glow();

        return builder.build();
    }

    private ItemStack createAnvilItem() {
        ItemBuilder builder = new ItemBuilder(Material.ANVIL)
                .name("&6✦ Enchantment Anvil ✦")
                .lore(
                        "&7Combine two identical",
                        "&7enchantment books to",
                        "&7increase their level!",
                        "",
                        "&7Cost: &aVaries",
                        "",
                        "&eClick to open!"
                );

        return builder.build();
    }

    private ItemStack createPurchaseItem(EnchantmentRarity rarity) {
        ItemBuilder builder = new ItemBuilder(Material.BOOK)
                .name(rarity.format("✦ " + rarity.getDisplayName() + " Enchantment ✦"))
                .lore(
                        "&7Purchase a random",
                        rarity.format(rarity.getDisplayName()) + " &7enchantment",
                        "",
                        "&7Cost: &a" + rarity.getXpCost() + " Levels", // Fixed this line
                        "",
                        "&eClick to purchase!"
                );

        return builder.build();
    }

    private void addInfoItem() {
        ItemStack info = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = info.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "✦ Information ✦");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Welcome to " + ChatColor.LIGHT_PURPLE + "DesEnchants!");
        lore.add("");
        lore.add(ChatColor.YELLOW + "How to use:");
        lore.add(ChatColor.WHITE + "• " + ChatColor.GRAY + "Purchase enchantment books");
        lore.add(ChatColor.WHITE + "• " + ChatColor.GRAY + "Drag books onto items to enchant");
        lore.add(ChatColor.WHITE + "• " + ChatColor.GRAY + "Browse categories for all enchants");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Rarities:");
        lore.add(ChatColor.GRAY + "• Common: " + ChatColor.GREEN + "20 Levels");
        lore.add(ChatColor.AQUA + "• Rare: " + ChatColor.GREEN + "40 Levels");
        lore.add(ChatColor.GOLD + "• Legendary: " + ChatColor.GREEN + "60 Levels");

        meta.setLore(lore);
        info.setItemMeta(meta);

        inventory.setItem(40, info);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        plugin.getGuiManager().setOpenGUI(player, "main");  // <-- THIS LINE fixes the issue
        player.openInventory(inventory);

        // Play sound if enabled
        if (plugin.getConfig().getBoolean("gui.enable-sounds", true)) {
            String soundName = plugin.getConfig().getString("gui.sounds.open-gui", "BLOCK_ENCHANTMENT_TABLE_USE");
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
            } catch (IllegalArgumentException e) {
                // Invalid sound
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}