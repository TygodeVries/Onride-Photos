package dev.thesheep.onridePhotos.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FancyParticles {

    public static void drawParticleCube(Location location, Player player)
    {
        World world = location.getWorld();
        if (world == null) return;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        Location a = new Location(world, x, y, z);
        Location b = new Location(world,x + 1, y, z);
        Location c = new Location(world, x,y + 1, z);
        Location d = new Location(world, x,y,z + 1);
        Location e = new Location(world,x + 1, y + 1, z);
        Location f = new Location(world, x + 1, y,z + 1);
        Location g = new Location(world,x,y + 1, z + 1);
        Location h = new Location(world,x + 1, y + 1, z + 1);

        World w = world;

        drawLine(w, a, b, player);
        drawLine(w, a, d, player);
        drawLine(w, b, f, player);
        drawLine(w, d, f, player);

        drawLine(w, c, e, player);
        drawLine(w, c, g, player);
        drawLine(w, e, h, player);
        drawLine(w, g, h, player);

        drawLine(w, a, c, player);
        drawLine(w, b, e, player);
        drawLine(w, d, g, player);
        drawLine(w, f, h, player);
    }

    private static void drawLine(World world, Location start, Location end, Player player)
    {
        double distance = start.distance(end);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        for (double i = 0; i <= distance; i += 0.2)
        {
            Location point = start.clone().add(direction.clone().multiply(i));

            player.spawnParticle(
                    Particle.REDSTONE,
                    point,
                    1,
                    new Particle.DustOptions(Color.RED, 1.0f)
            );
        }
    }
}
