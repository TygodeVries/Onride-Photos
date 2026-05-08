package dev.thesheep.onridePhotos.database;

public interface Database {

    void uploadPhoto(ImageMetadata metadata, byte[] file);
}
