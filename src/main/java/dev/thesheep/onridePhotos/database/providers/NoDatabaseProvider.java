package dev.thesheep.onridePhotos.database.providers;

import dev.thesheep.onridePhotos.database.Database;
import dev.thesheep.onridePhotos.database.ImageMetadata;

public class NoDatabaseProvider implements Database {
    @Override
    public void uploadPhoto(ImageMetadata metadata, byte[] file) {
        // Do nothing!
    }
}
