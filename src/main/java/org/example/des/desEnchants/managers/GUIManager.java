package org.example.des.desEnchants.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.example.des.desEnchants.DesEnchants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    private final DesEnchants plugin;
    private final Map<UUID, String> openGUIs = new HashMap<>();

    public GUIManager(DesEnchants plugin) {
        this.plugin = plugin;
    }

    /**
     * Register that a player has opened a specific GUI
     */
    public void setOpenGUI(Player player, String guiType) {
        openGUIs.put(player.getUniqueId(), guiType);
    }

    /**
     * Get the GUI type a player currently has open
     */
    public String getOpenGUI(Player player) {
        return openGUIs.get(player.getUniqueId());
    }

    /**
     * Remove a player's GUI tracking (when they close it)
     */
    public void removeOpenGUI(Player player) {
        openGUIs.remove(player.getUniqueId());
    }

    /**
     * Check if a player has a specific GUI open
     */
    public boolean hasGUIOpen(Player player, String guiType) {
        return guiType.equals(openGUIs.get(player.getUniqueId()));
    }

    /**
     * Create an inventory with a specific holder
     */
    public Inventory createInventory(InventoryHolder holder, int size, String title) {
        // Ensure size is a multiple of 9
        if (size % 9 != 0 || size < 9 || size > 54) {
            size = 54; // Default to double chest size
        }

        return Bukkit.createInventory(holder, size, title);
    }

    /**
     * Close all open GUIs for all players
     */
    public void closeAllGUIs() {
        for (UUID uuid : openGUIs.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
        openGUIs.clear();
    }

    /**
     * Get the number of players with GUIs open
     */
    public int getOpenGUICount() {
        return openGUIs.size();
    }
}