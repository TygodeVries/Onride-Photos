package dev.thesheep.onridePhotos.content;

import dev.thesheep.onridePhotos.dependency.UUIDFixer;

import java.util.UUID;

public class Face {

    private final UUID playerUuid;

    /**
     * Create a face from a player UUID
     * @param playerUuid
     */
    public Face(UUID playerUuid)
    {
        this.playerUuid = playerUuid;
    }

    /**
     * The UUID of the player who this face belongs to.
     * @return
     */
    public UUID getPlayerUUID()
    {
        return playerUuid;
    }
}
