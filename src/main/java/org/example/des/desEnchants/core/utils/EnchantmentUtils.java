package org.example.des.desEnchants.core.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.example.des.desEnchants.DesEnchants;
import org.example.des.desEnchants.core.enchantment.CustomEnchantment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EnchantmentUtils {

    private static final DesEnchants plugin = DesEnchants.getInstance();
    private static final NamespacedKey BOOK_KEY = new NamespacedKey(plugin, "enchanted_book");
    private static final Gson gson = new Gson();

    public static boolean isEnchantmentBook(ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(BOOK_KEY, PersistentDataType.STRING);
    }

    public static CustomEnchantment getEnchantmentFromBook(ItemStack book) {
        if (!isEnchantmentBook(book)) {
            return null;
        }

        ItemMeta meta = book.getItemMeta();
        String data = meta.getPersistentDataContainer().get(BOOK_KEY, PersistentDataType.STRING);

        if (data == null) {
            return null;
        }

        JsonObject json = gson.fromJson(data, JsonObject.class);
        String enchantmentId = json.get("enchantment").getAsString();

        return plugin.getEnchantmentManager().getEnchantment(enchantmentId);
    }

    public static int getEnchantmentLevelFromBook(ItemStack book) {
        if (!isEnchantmentBook(book)) {
            return 0;
        }

        ItemMeta meta = book.getItemMeta();
        String data = meta.getPersistentDataContainer().get(BOOK_KEY, PersistentDataType.STRING);

        if (data == null) {
            return 0;
        }

        JsonObject json = gson.fromJson(data, JsonObject.class);
        return json.get("level").getAsInt();
    }

    public static int getSuccessRate(ItemStack book) {
        if (!isEnchantmentBook(book)) {
            return 0;
        }

        ItemMeta meta = book.getItemMeta();
        String data = meta.getPersistentDataContainer().get(BOOK_KEY, PersistentDataType.STRING);

        if (data == null) {
            return 0;
        }

        JsonObject json = gson.fromJson(data, JsonObject.class);
        return json.get("success_rate").getAsInt();
    }

    public static int getDestroyRate(ItemStack book) {
        if (!isEnchantmentBook(book)) {
            return 0;
        }

        ItemMeta meta = book.getItemMeta();
        String data = meta.getPersistentDataContainer().get(BOOK_KEY, PersistentDataType.STRING);

        if (data == null) {
            return 0;
        }

        JsonObject json = gson.fromJson(data, JsonObject.class);
        return json.get("destroy_rate").getAsInt();
    }

    public static ItemStack createEnchantmentBook(CustomEnchantment enchantment, int level, int successRate, int destroyRate) {
        ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK);

        // Set display name
        builder.setName("&e&l" + enchantment.getDisplayName() + " " + level);

        // Set lore
        builder.addLore("&7Success Rate: &a" + successRate + "%");
        builder.addLore("&7Destroy Rate: &c" + destroyRate + "%");
        builder.addLore("");
        for (String line : enchantment.getDescription()) {
            builder.addLore(line);
        }
        builder.addLore("");
        builder.addLore("&7Drag n drop onto item to enchant");

        // Build item
        ItemStack book = builder.build();

        // Store enchantment data
        JsonObject data = new JsonObject();
        data.addProperty("enchantment", enchantment.getId());
        data.addProperty("level", level);
        data.addProperty("success_rate", successRate);
        data.addProperty("destroy_rate", destroyRate);

        ItemMeta meta = book.getItemMeta();
        meta.getPersistentDataContainer().set(BOOK_KEY, PersistentDataType.STRING, gson.toJson(data));
        book.setItemMeta(meta);

        return book;
    }
}