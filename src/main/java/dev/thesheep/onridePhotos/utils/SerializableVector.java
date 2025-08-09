package dev.thesheep.onridePhotos.utils;

import org.bukkit.util.Vector;

public class SerializableVector {
    public double x, y, z;

    public SerializableVector(Vector vec) {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}