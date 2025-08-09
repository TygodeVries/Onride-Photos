package dev.thesheep.onridePhotos;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import dev.thesheep.onridePhotos.commands.PhotoCommand;
import dev.thesheep.onridePhotos.content.Face;
import dev.thesheep.onridePhotos.content.FaceLayout;
import dev.thesheep.onridePhotos.content.FaceType;
import dev.thesheep.onridePhotos.content.PhotoLayout;
import dev.thesheep.onridePhotos.display.PhotoDisplay;
import dev.thesheep.onridePhotos.signs.PhotoSign;
import dev.thesheep.onridePhotos.signs.TCPhotoSign;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;

public final class OnridePhotos extends JavaPlugin {

    private static OnridePhotos instance;
    public static OnridePhotos getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        File imageFolder = new File(getDataFolder() + "/image");
        if(!imageFolder.exists())
        {
            imageFolder.mkdirs();
        }

        File layoutFolder = new File(getDataFolder() + "/layout");
        if(!layoutFolder.exists())
        {
            layoutFolder.mkdirs();
        }

        PhotoDisplay.loadAll();
        SignAction.register(new TCPhotoSign());

        this.getCommand("photo").setExecutor(new PhotoCommand());
        this.getServer().getPluginManager().registerEvents(new PhotoSign(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
