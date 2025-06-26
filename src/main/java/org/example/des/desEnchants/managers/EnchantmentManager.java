package org.example.des.desEnchants.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.enchantments.armor.chestplate.PhoenixEnchantment;
import org.example.des.desEnchants.enchantments.weapons.shared.LifestealEnchantment;
import org.example.des.desEnchants.enchantments.weapons.shared.LightningEnchantment;
import org.example.des.desEnchants.enchantments.tools.pickaxe.HasteEnchantment;
import org.example.des.desEnchants.enchantments.tools.pickaxe.AutosmeltEnchantment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;
import java.util.stream.Collectors;

public class EnchantmentManager {

    private final DesEnchants plugin;
    private final Map<String, CustomEnchantment> enchantments = new HashMap<>();
    private final Map<EnchantmentRarity, List<CustomEnchantment>> enchantmentsByRarity = new HashMap<>();
    private final NamespacedKey enchantmentKey;
    private final Gson gson = new Gson();
    private final Random random = new Random(); // Add this field

    public EnchantmentManager(DesEnchants plugin) {
        this.plugin = plugin;
        this.enchantmentKey = new NamespacedKey(plugin, "enchantments");

        // Initialize rarity lists
        for (EnchantmentRarity rarity : EnchantmentRarity.values()) {
            enchantmentsByRarity.put(rarity, new ArrayList<>());
        }
    }

    // Add getter for random
    public Random getRandom() {
        return random;
    }

    public void loadEnchantments() {
        // Clear existing enchantments
        unloadEnchantments();

        // Register enchantments
        registerEnchantment(new PhoenixEnchantment(plugin));
        registerEnchantment(new LifestealEnchantment(plugin));
        registerEnchantment(new LightningEnchantment(plugin));
        registerEnchantment(new HasteEnchantment(plugin));
        registerEnchantment(new AutosmeltEnchantment(plugin));

        plugin.getLogger().info("Loaded " + enchantments.size() + " custom enchantments");
    }

    public void unloadEnchantments() {
        enchantments.clear();
        enchantmentsByRarity.values().forEach(List::clear);
    }

    public void reload() {
        loadEnchantments();
    }

    private void registerEnchantment(CustomEnchantment enchantment) {
        String id = enchantment.getId();

        if (enchantments.containsKey(id)) {
            plugin.getLogger().warning("Duplicate enchantment ID: " + id);
            return;
        }

        // Initialize the enchantment
        enchantment.initialize();

        // Add to maps
        enchantments.put(id, enchantment);
        enchantmentsByRarity.get(enchantment.getRarity()).add(enchantment);

        // Register listener if the enchantment implements Listener
        if (enchantment instanceof org.bukkit.event.Listener) {
            plugin.getServer().getPluginManager().registerEvents(
                    (org.bukkit.event.Listener) enchantment, plugin
            );
        }

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("Registered enchantment: " + enchantment.getDisplayName());
        }
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

    public Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        Map<CustomEnchantment, Integer> result = new HashMap<>();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return result;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(enchantmentKey, PersistentDataType.STRING)) {
            return result;
        }

        String json = container.get(enchantmentKey, PersistentDataType.STRING);
        Map<String, Integer> data = gson.fromJson(json,
                new TypeToken<Map<String, Integer>>(){}.getType());

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            CustomEnchantment enchant = getEnchantment(entry.getKey());
            if (enchant != null && enchant.isEnabled()) {
                result.put(enchant, entry.getValue());
            }
        }

        return result;
    }

    public void addEnchantment(ItemStack item, CustomEnchantment enchantment, int level) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        Map<CustomEnchantment, Integer> current = getEnchantments(item);

        // Add or update enchantment
        current.put(enchantment, Math.min(level, enchantment.getMaxLevel()));

        // Convert to storage format
        Map<String, Integer> data = current.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getId(),
                        Map.Entry::getValue
                ));

        // Save to item
        meta.getPersistentDataContainer().set(
                enchantmentKey,
                PersistentDataType.STRING,
                gson.toJson(data)
        );

        // Update lore
        updateItemLore(meta, current);

        item.setItemMeta(meta);
    }

    public void removeEnchantment(ItemStack item, CustomEnchantment enchantment) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        Map<CustomEnchantment, Integer> current = getEnchantments(item);

        if (!current.containsKey(enchantment)) return;

        current.remove(enchantment);

        if (current.isEmpty()) {
            // Remove all enchantment data
            meta.getPersistentDataContainer().remove(enchantmentKey);
        } else {
            // Update enchantment data
            Map<String, Integer> data = current.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().getId(),
                            Map.Entry::getValue
                    ));

            meta.getPersistentDataContainer().set(
                    enchantmentKey,
                    PersistentDataType.STRING,
                    gson.toJson(data)
            );
        }

        // Update lore
        updateItemLore(meta, current);

        item.setItemMeta(meta);
    }

    private void updateItemLore(ItemMeta meta, Map<CustomEnchantment, Integer> enchantments) {
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        // Remove old enchantment lore
        lore.removeIf(line -> enchantments.keySet().stream()
                .anyMatch(e -> line.contains(e.getDisplayName())));

        // Add new enchantment lore
        List<String> enchantLore = new ArrayList<>();
        for (Map.Entry<CustomEnchantment, Integer> entry : enchantments.entrySet()) {
            String level = entry.getValue() > 1 ? " " + entry.getValue() : "";
            enchantLore.add("§7" + entry.getKey().getDisplayName() + level);
        }

        // Add enchantment lore at the beginning
        lore.addAll(0, enchantLore);

        meta.setLore(lore);
    }

    public boolean hasEnchantment(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantments(item).containsKey(enchantment);
    }

    public int getEnchantmentLevel(ItemStack item, CustomEnchantment enchantment) {
        return getEnchantments(item).getOrDefault(enchantment, 0);
    }

    public CustomEnchantment getRandomEnchantment(EnchantmentRarity rarity) {
        List<CustomEnchantment> available = enchantmentsByRarity.get(rarity).stream()
                .filter(CustomEnchantment::isEnabled)
                .collect(Collectors.toList());

        if (available.isEmpty()) return null;

        return available.get(random.nextInt(available.size()));
    }
}