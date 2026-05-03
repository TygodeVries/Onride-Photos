package dev.thesheep.onridePhotos;

import com.bergerkiller.bukkit.common.collections.EntryList;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import dev.thesheep.onridePhotos.commands.PhotoCommand;
import dev.thesheep.onridePhotos.content.Face;
import dev.thesheep.onridePhotos.content.FaceLayout;
import dev.thesheep.onridePhotos.content.FaceType;
import dev.thesheep.onridePhotos.content.PhotoLayout;
import dev.thesheep.onridePhotos.display.DisplaySelector;
import dev.thesheep.onridePhotos.display.PhotoDisplay;
import dev.thesheep.onridePhotos.listeners.PhotoUpdater;
import dev.thesheep.onridePhotos.listeners.UsePhotoDisplay;
import dev.thesheep.onridePhotos.signs.PhotoSign;
import dev.thesheep.onridePhotos.signs.TCPhotoSign;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OnridePhotos extends JavaPlugin {

    public static ArrayList<Integer> activeMaps = new ArrayList<Integer>();
    private static OnridePhotos instance;
    public static OnridePhotos getInstance()
    {
        return instance;
    }
    public static boolean skinsRestorerInstalled;
    @Override
    public void onEnable() {
        // Plugin startup logic

        // Check if we have a skin backup
        skinsRestorerInstalled =
                Bukkit.getPluginManager().getPlugin("SkinsRestorer") != null;

        // Send a little message to inform users of SkinsRestorer
        if(Bukkit.getOnlineMode())
        {
            this.getLogger().info("Your server is running in online mode, everything should function correctly.");
        }
        else {
            if (skinsRestorerInstalled)
            {
                try {
                    SkinsRestorer sr = SkinsRestorerProvider.get();
                }
                catch (Exception e)
                {
                    skinsRestorerInstalled = false;
                    this.getLogger().info("Your server is running in offline mode, but you have SkinsRestorer installed. However, its not working as it should, and can not be used. This is because " + e);
                }

                if(skinsRestorerInstalled)
                    this.getLogger().info("Your server is running in offline mode, but you have SkinsRestorer installed so everything should function correctly.");
            }
            else {
                this.getLogger().severe("You are running in offline mode, this might break some things, if it does, you can try installing SkinsRestorer.");
            }
        }

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

        // Get the fixer ready!
        this.getServer().getPluginManager().registerEvents(new PhotoUpdater(), this);

        this.getServer().getPluginManager().registerEvents(new UsePhotoDisplay(), this);
        DisplaySelector.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
