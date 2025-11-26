package dev.thesheep.onridePhotos.content;

import dev.thesheep.onridePhotos.dependency.UUIDFixer;

import java.util.UUID;

public class Face {

    private final UUID playerUuid;
    public Face(UUID playerUuid)
    {
        this.playerUuid = playerUuid;
    }

    public UUID getPlayerUUID()
    {
        return playerUuid;
    }
}
