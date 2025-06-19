package org.example.des.desEnchants.core.utils;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class NBTHelper {

    /**
     * Get or create a compound tag
     */
    public static NBTCompound getOrCreateCompound(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getOrCreateCompound(key);
    }

    /**
     * Check if item has a specific compound
     */
    public static boolean hasCompound(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasTag(key);
    }

    /**
     * Get compound from item
     */
    public static NBTCompound getCompound(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getCompound(key);
    }

    /**
     * Set a string value in NBT
     */
    public static ItemStack setString(ItemStack item, String compound, String key, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.getOrCreateCompound(compound).setString(key, value);
        return nbtItem.getItem();
    }

    /**
     * Get a string value from NBT
     */
    public static String getString(ItemStack item, String compound, String key) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag(compound)) return null;
        return nbtItem.getCompound(compound).getString(key);
    }

    /**
     * Set an integer value in NBT
     */
    public static ItemStack setInt(ItemStack item, String compound, String key, int value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.getOrCreateCompound(compound).setInteger(key, value);
        return nbtItem.getItem();
    }

    /**
     * Get an integer value from NBT
     */
    public static int getInt(ItemStack item, String compound, String key) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag(compound)) return 0;
        return nbtItem.getCompound(compound).getInteger(key);
    }

    /**
     * Remove a compound from item
     */
    public static ItemStack removeCompound(ItemStack item, String compound) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(compound);
        return nbtItem.getItem();
    }

    /**
     * Get all keys in a compound
     */
    public static Set<String> getKeys(ItemStack item, String compound) {
        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag(compound)) return Set.of();
        return nbtItem.getCompound(compound).getKeys();
    }
}