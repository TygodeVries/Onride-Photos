package dev.thesheep.onridePhotos.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableLocation {
    public String world;
    public double x, y, z;
    public float pitch, yaw;

    public SerializableLocation(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.pitch = loc.getPitch();
        this.yaw = loc.getYaw();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}