package dev.thesheep.onridePhotos.dependency;

import dev.thesheep.onridePhotos.OnridePhotos;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class UUIDFixer {
    public static UUID getWrappedUUID(OfflinePlayer offlinePlayer)
    {
        if(!OnridePhotos.skinsRestorerInstalled)
        {
            return offlinePlayer.getUniqueId();
        }

        try {
            SkinsRestorer sr = SkinsRestorerProvider.get();

            var uuidGet = sr.getMojangAPI().getUUID(offlinePlayer.getName());

            if(uuidGet.isPresent())
            {
               return uuidGet.get();
            }

            // Fallback
            return offlinePlayer.getUniqueId();
        } catch (Exception e)
        {
            Bukkit.getLogger().severe("Could not get real UUID of player " + offlinePlayer.getName() + " with UUID " + offlinePlayer.getUniqueId() + " because: " + e);
        }

        // Fallback
        return offlinePlayer.getUniqueId();
    }
}
