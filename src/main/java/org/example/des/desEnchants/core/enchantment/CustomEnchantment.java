package org.example.des.desEnchants.core.enchantment;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;

import java.util.List;
import java.util.Random;

public abstract class CustomEnchantment {

    protected final DesEnchants plugin;
    private final String id;
    private final String displayName;
    private final int maxLevel;
    private final EnchantmentRarity rarity;
    private final EnchantmentTarget target;
    private final Random random = new Random();

    // Configuration values
    protected boolean enabled = true;
    protected double activationChance = 100.0;
    protected int cooldown = 0;
    protected List<String> description;

    // Success and Destroy rates
    private final int successRate;
    private final int destroyRate;

    public CustomEnchantment(DesEnchants plugin, String id, String displayName,
                             int maxLevel, EnchantmentRarity rarity, EnchantmentTarget target) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.target = target;

        // Generate random success/destroy rates
        this.successRate = random.nextInt(101);
        this.destroyRate = random.nextInt(101);
    }

    /**
     * Called when the enchantment effect should be triggered
     */
    public abstract boolean onTrigger(Event event, Player player, ItemStack item, int level);

    /**
     * Check if this enchantment can be applied to an item
     */
    public boolean canApplyTo(ItemStack item) {
        return target.isValidItem(item);
    }

    /**
     * Check if the enchantment effect can activate
     */
    protected boolean canActivate(Player player) {
        // Check cooldown
        if (cooldown > 0 && plugin.getCooldownManager().isOnCooldown(player, id)) {
            return false;
        }

        // Check activation chance
        if (Math.random() * 100 > activationChance) {
            return false;
        }

        return true;
    }

    /**
     * Apply cooldown after successful effect activation
     */
    protected void applyCooldown(Player player) {
        if (cooldown > 0) {
            plugin.getCooldownManager().setCooldown(player, id, cooldown);
        }
    }

    // All the getters...
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getMaxLevel() { return maxLevel; }
    public EnchantmentRarity getRarity() { return rarity; }
    public EnchantmentTarget getTarget() { return target; }
    public boolean isEnabled() { return enabled; }
    public double getActivationChance() { return activationChance; }
    public int getCooldown() { return cooldown; }
    public List<String> getDescription() { return description; }
    public int getSuccessRate() { return successRate; }
    public int getDestroyRate() { return destroyRate; }

    // Setters for configuration
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setActivationChance(double chance) { this.activationChance = chance; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
    public void setDescription(List<String> description) { this.description = description; }
}