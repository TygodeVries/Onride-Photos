package dev.thesheep.onridePhotos.content;

import dev.thesheep.onridePhotos.dependency.UUIDFixer;

import java.util.UUID;

public class Face {

    private final UUID playerUuid;
    private final String username;

    /**
     * Create a face from a player UUID
     * @param playerUuid
     */
    public Face(UUID playerUuid, String username)
    {
        this.playerUuid = playerUuid;
        this.username = username;
    }

    /**
     * The UUID of the player who this face belongs to.
     * @return
     */
    public UUID getPlayerUUID()
    {
        return playerUuid;
    }
    public String getPlayerName() {return username;}
}
