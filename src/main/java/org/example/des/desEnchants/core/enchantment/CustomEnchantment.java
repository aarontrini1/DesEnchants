package org.example.des.desEnchants.core.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.example.des.desEnchants.DesEnchants;

import java.util.List;
import java.util.Random;

public abstract class CustomEnchantment {

    protected final DesEnchants plugin;
    protected final Random random = new Random();

    private final String id;
    private final String displayName;
    private final int maxLevel;
    private final EnchantmentRarity rarity;
    private final EnchantmentTarget target;
    private final List<String> description;
    private final Sound applySound;
    private final int baseChance;
    private final int chancePerLevel;
    private boolean enabled = true;

    public CustomEnchantment(DesEnchants plugin, String id, String displayName,
                             int maxLevel, EnchantmentRarity rarity,
                             EnchantmentTarget target, List<String> description) {
        this.plugin = plugin;
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.target = target;
        this.description = description;
        this.applySound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        this.baseChance = 100;
        this.chancePerLevel = 0;
    }

    public CustomEnchantment(DesEnchants plugin, String id, String displayName,
                             int maxLevel, EnchantmentRarity rarity,
                             EnchantmentTarget target, List<String> description,
                             Sound applySound, int baseChance, int chancePerLevel) {
        this.plugin = plugin;
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.target = target;
        this.description = description;
        this.applySound = applySound;
        this.baseChance = baseChance;
        this.chancePerLevel = chancePerLevel;
    }

    // Abstract method for enchantment-specific initialization
    public abstract void initialize();

    // Check if enchantment should activate based on chance
    public boolean shouldActivate(int level) {
        if (baseChance >= 100 && chancePerLevel == 0) return true;

        int chance = baseChance + (chancePerLevel * (level - 1));
        return random.nextInt(100) < chance;
    }

    // Check if item can be enchanted
    public boolean canEnchantItem(ItemStack item) {
        return target != null && target.canEnchant(item);
    }

    // Check if player has permission
    public boolean hasPermission(Player player) {
        return player.hasPermission("desenchants.enchantment." + id);
    }

    // Store data on item
    protected void storeData(ItemStack item, String key, String value) {
        NamespacedKey dataKey = new NamespacedKey(plugin, "enchant_" + id + "_" + key);
        item.getItemMeta().getPersistentDataContainer().set(dataKey, PersistentDataType.STRING, value);
    }

    // Retrieve data from item
    protected String retrieveData(ItemStack item, String key) {
        NamespacedKey dataKey = new NamespacedKey(plugin, "enchant_" + id + "_" + key);
        return item.getItemMeta().getPersistentDataContainer().get(dataKey, PersistentDataType.STRING);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public EnchantmentRarity getRarity() {
        return rarity;
    }

    public EnchantmentTarget getTarget() {
        return target;
    }

    public List<String> getDescription() {
        return description;
    }

    public Sound getApplySound() {
        return applySound;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}