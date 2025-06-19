package org.example.des.desEnchants.shared.effects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

public class SoundHelper {

    private static final Random random = new Random();

    /**
     * Play a sound with random pitch variation
     */
    public static void playWithVariation(Player player, Sound sound, float volume, float basePitch, float variation) {
        float pitch = basePitch + (random.nextFloat() * variation * 2 - variation);
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /**
     * Play a sound at a specific location
     */
    public static void playAtLocation(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    /**
     * Play an ascending pitch sound effect
     */
    public static void playAscending(Player player, Sound sound, float volume, int steps, long delayTicks) {
        for (int i = 0; i < steps; i++) {
            final int step = i;
            player.getServer().getScheduler().runTaskLater(
                    player.getServer().getPluginManager().getPlugin("DesEnchants"),
                    () -> player.playSound(player.getLocation(), sound, volume, 0.5f + (step * 0.2f)),
                    i * delayTicks
            );
        }
    }

    /**
     * Play a descending pitch sound effect
     */
    public static void playDescending(Player player, Sound sound, float volume, int steps, long delayTicks) {
        for (int i = 0; i < steps; i++) {
            final int step = i;
            player.getServer().getScheduler().runTaskLater(
                    player.getServer().getPluginManager().getPlugin("DesEnchants"),
                    () -> player.playSound(player.getLocation(), sound, volume, 2.0f - (step * 0.2f)),
                    i * delayTicks
            );
        }
    }

    /**
     * Play a success sound combo
     */
    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
    }

    /**
     * Play a failure sound combo
     */
    public static void playFailure(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1.5f);
    }

    /**
     * Play an activation sound
     */
    public static void playActivation(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
    }
}