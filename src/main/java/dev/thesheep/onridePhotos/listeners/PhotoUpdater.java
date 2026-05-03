package dev.thesheep.onridePhotos.listeners;

import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.content.Face;
import dev.thesheep.onridePhotos.content.Photo;
import dev.thesheep.onridePhotos.content.PhotoLayout;
import dev.thesheep.onridePhotos.content.PhotoRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;

public class PhotoUpdater implements Listener {
    @EventHandler
    public void on(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        ItemStack item = player.getInventory().getItem(newSlot);

        if (item == null) return;

        if (item.getType() != Material.FILLED_MAP)
            return;

        MapMeta map = (MapMeta) item.getItemMeta();

        if (map == null)
            return;


        if (!map.hasMapView()) {
            return; // corrupt?
        }

        if (map.getMapView() == null) {
            return;
        }

        if (OnridePhotos.activeMaps.contains((Integer) map.getMapView().getId()))
            return;

        OnridePhotos.activeMaps.add((Integer) map.getMapView().getId());

        String layout = map.getPersistentDataContainer().get(new NamespacedKey(OnridePhotos.getInstance(), "layout"), PersistentDataType.STRING);
        if(layout == null)
            return; // can't fix this.


        PhotoLayout photoLayout = null;

        if (layout.startsWith("file:")) {
            String path = layout.substring("file:".length()); // remove header

            File layoutFile = new File(path);
            if (!layoutFile.exists()) {
                OnridePhotos.getInstance().getLogger().severe(
                        "Attempted to restore a picture, but the layout was removed! " + layout
                );
                return;
            }

            photoLayout = PhotoLayout.loadFromFile(layoutFile);
        }

        if(photoLayout == null)
        {
            OnridePhotos.getInstance().getLogger().severe("Could not find a way to fix the photo with layout: " + layout);
            return;
        }

        String guests = map.getPersistentDataContainer().get(new NamespacedKey(OnridePhotos.getInstance(), "guests"), PersistentDataType.STRING);

        if(guests == null)
            guests = "";

        String[] guestIds = guests.split(";");

        Face[] faces = new Face[guestIds.length];

        for(int i = 0; i < guestIds.length; i++)
        {
            String id = guestIds[i];

            OfflinePlayer guest = Bukkit.getPlayer(id);
            String username = "?";
            if(guest != null)
            {
                username = guest.getName();
            }

            if(id.isBlank() || id.isEmpty() || id.length() < 5)
                continue;

            try {
                faces[i] = new Face(UUID.fromString(id), username);
            } catch (Exception e) {
                OnridePhotos.getInstance().getLogger().warning("Failed to fix picture, UUID '" + id + "' is invalid!");
            }
        }

        Photo photo = new Photo(photoLayout, faces);

        photo.whenDone(new BukkitRunnable() {
            @Override
            public void run() {

                map.getMapView().addRenderer(new PhotoRenderer(photo.getResult()));
                item.setItemMeta(map);
            }
        });

        photo.renderImage();

    }
}
