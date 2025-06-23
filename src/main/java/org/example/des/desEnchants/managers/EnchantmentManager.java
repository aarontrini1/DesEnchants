package org.example.des.desEnchants.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.enchantments.armor.chestplate.PhoenixEnchantment;
import org.example.des.desEnchants.enchantments.tools.pickaxe.AutosmeltEnchantment;
import org.example.des.desEnchants.enchantments.tools.pickaxe.HasteEnchantment;
import org.example.des.desEnchants.enchantments.weapons.shared.LifestealEnchantment;
import org.example.des.desEnchants.enchantments.weapons.shared.LightningEnchantment;

import java.util.*;

public class EnchantmentManager {

    private final DesEnchants plugin;
    private final Map<String, CustomEnchantment> enchantments = new HashMap<>();
    private final Map<EnchantmentRarity, List<CustomEnchantment>> enchantmentsByRarity = new HashMap<>();

    public EnchantmentManager(DesEnchants plugin) {
        this.plugin = plugin;

        // Initialize rarity lists
        for (EnchantmentRarity rarity : EnchantmentRarity.values()) {
            enchantmentsByRarity.put(rarity, new ArrayList<>());
        }
    }

    public void registerEnchantment(CustomEnchantment enchantment) {
        String id = enchantment.getId().toLowerCase();

        if (enchantments.containsKey(id)) {
            plugin.getLogger().warning("Enchantment with ID '" + id + "' is already registered!");
            return;
        }

        enchantments.put(id, enchantment);
        enchantmentsByRarity.get(enchantment.getRarity()).add(enchantment);

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("Registered enchantment: " + enchantment.getDisplayName());
        }
    }

    public void loadEnchantments() {
        // Armor enchantments
        registerEnchantment(new PhoenixEnchantment(plugin));

        // Weapon enchantments
        registerEnchantment(new LifestealEnchantment(plugin));
        registerEnchantment(new LightningEnchantment(plugin));

        // Tool enchantments
        registerEnchantment(new HasteEnchantment(plugin));
        registerEnchantment(new AutosmeltEnchantment(plugin));

        plugin.getLogger().info("Loaded " + enchantments.size() + " custom enchantments");
    }

    public void reload() {
        enchantments.clear();
        for (List<CustomEnchantment> list : enchantmentsByRarity.values()) {
            list.clear();
        }
        loadEnchantments();
    }

    public CustomEnchantment getEnchantment(String id) {
        return enchantments.get(id.toLowerCase());
    }

    public Collection<CustomEnchantment> getAllEnchantments() {
        return Collections.unmodifiableCollection(enchantments.values());
    }

    public List<CustomEnchantment> getEnchantmentsByRarity(EnchantmentRarity rarity) {
        return Collections.unmodifiableList(enchantmentsByRarity.get(rarity));
    }

    public CustomEnchantment getRandomEnchantment(EnchantmentRarity rarity) {
        List<CustomEnchantment> available = enchantmentsByRarity.get(rarity);
        if (available.isEmpty()) return null;

        Random random = new Random();
        return available.get(random.nextInt(available.size()));
    }

    public int getRegisteredCount() {
        return enchantments.size();
    }

    public Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        Map<CustomEnchantment, Integer> result = new HashMap<>();

        if (item == null || item.getType() == Material.AIR) {
            return result;
        }

        // We'll implement the actual NBT reading logic later
        // For now, return empty map

        return result;
    }

    public boolean hasEnchantment(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantments(item).containsKey(enchantment);
    }

    public int getEnchantmentLevel(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantments(item).getOrDefault(enchantment, 0);
    }
}