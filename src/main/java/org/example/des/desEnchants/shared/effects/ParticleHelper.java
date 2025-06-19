package org.example.des.desEnchants.shared.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleHelper {

    /**
     * Create a circle of particles around a location
     */
    public static void playCircle(Location center, Particle particle, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location point = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Create a helix effect around a player
     */
    public static void playHelix(Player player, Particle particle, double radius, double height, int points) {
        Location loc = player.getLocation();
        for (int i = 0; i < points; i++) {
            double y = height * i / points;
            double angle = 2 * Math.PI * i / 10;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            loc.add(x, y, z);
            player.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
            loc.subtract(x, y, z);
        }
    }

    /**
     * Create a burst effect at a location
     */
    public static void playBurst(Location location, Particle particle, int count, double speed) {
        location.getWorld().spawnParticle(particle, location, count, 0.5, 0.5, 0.5, speed);
    }

    /**
     * Create a line of particles between two locations
     */
    public static void playLine(Location start, Location end, Particle particle, int points) {
        Vector direction = end.toVector().subtract(start.toVector());
        double length = direction.length();
        direction.normalize();

        for (int i = 0; i <= points; i++) {
            double distance = length * i / points;
            Location point = start.clone().add(direction.clone().multiply(distance));
            start.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }

    /**
     * Create a dome shield effect
     */
    public static void playDome(Location center, Particle particle, double radius, int density) {
        for (double phi = 0; phi <= Math.PI / 2; phi += Math.PI / density) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / density) {
                double x = radius * Math.cos(theta) * Math.sin(phi);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(theta) * Math.sin(phi);

                Location point = center.clone().add(x, y, z);
                center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
            }
        }
    }
}