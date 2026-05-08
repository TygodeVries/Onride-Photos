package dev.thesheep.onridePhotos.database;

public class ImageMetadata {
    public String layout;
    public String[] players;

    public ImageMetadata(String layout, String[] players)
    {
        this.layout = layout;
        this.players = players;
    }
}
