package org.example.des.desEnchants.enchantments.tools.pickaxe;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import org.example.des.desEnchants.core.enchantment.EnchantmentRarity;
import org.example.des.desEnchants.core.enchantment.EnchantmentTarget;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AutosmeltEnchantment extends CustomEnchantment implements Listener {

    private final Map<Material, Material> smeltMap = new HashMap<>();

    public AutosmeltEnchantment(DesEnchants plugin) {
        super(plugin, "autosmelt", "Autosmelt", 1,
                EnchantmentRarity.RARE,
                EnchantmentTarget.PICKAXE,
                Arrays.asList(
                        "§7Automatically smelts ores",
                        "§7when you mine them."
                ));


        // Initialize smelt mappings
        smeltMap.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltMap.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        smeltMap.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        smeltMap.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        smeltMap.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        smeltMap.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        smeltMap.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        smeltMap.put(Material.RAW_IRON_BLOCK, Material.IRON_BLOCK);
        smeltMap.put(Material.RAW_GOLD_BLOCK, Material.GOLD_BLOCK);
        smeltMap.put(Material.RAW_COPPER_BLOCK, Material.COPPER_BLOCK);
        smeltMap.put(Material.SAND, Material.GLASS);
        smeltMap.put(Material.COBBLESTONE, Material.STONE);
        smeltMap.put(Material.STONE, Material.SMOOTH_STONE);
    }

    @Override
    public void initialize() {
        // Any initialization code specific to this enchantment
    }

    public String getLevelSpecificDescription(int level) {
        return "§7Smelts all compatible blocks";
    }

    @EventHandler
    public boolean onTrigger(BlockBreakEvent event, Player player, ItemStack item, int level) {
        if (!(event instanceof BlockBreakEvent blockBreakEvent)) {
            return false;
        }

        Block block = blockBreakEvent.getBlock();
        Material blockType = block.getType();

        // Check if block can be smelted
        if (!smeltMap.containsKey(blockType)) {
            return false;
        }

        // Cancel the normal drop
        blockBreakEvent.setDropItems(false);

        // Get the smelted result
        Material smeltedMaterial = smeltMap.get(blockType);
        ItemStack smeltedItem = new ItemStack(smeltedMaterial);

        // Handle fortune enchantment for ores
        if (blockType.name().contains("ORE") && !blockType.name().contains("IRON") && !blockType.name().contains("GOLD")) {
            Collection<ItemStack> originalDrops = block.getDrops(item);
            int totalAmount = originalDrops.stream().mapToInt(ItemStack::getAmount).sum();
            smeltedItem.setAmount(totalAmount);
        }

        // Drop the smelted item
        block.getWorld().dropItemNaturally(block.getLocation(), smeltedItem);

        // Add experience for smelting
        if (blockType.name().contains("ORE")) {
            blockBreakEvent.setExpToDrop(blockBreakEvent.getExpToDrop() + 1);
        }

        return true;
    }
}