package dev.thesheep.onridePhotos.content;

import dev.thesheep.onridePhotos.OnridePhotos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class Photo {

    PhotoLayout layout;
    Face[] faces;

    public Photo(PhotoLayout layout, Face[] faces)
    {
        this.layout = layout;
        this.faces = faces;
    }

    private BukkitRunnable callback;
    public void whenDone(BukkitRunnable callback)
    {
        this.callback = callback;
    }

    private BufferedImage result;
    public void renderImage()
    {
        // Download the images of the main thread to avoid blocking the server.
        BukkitRunnable downloadRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                BufferedImage canvas = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
                URL backgroundUrl;

                try {
                    File imageFile = new File(OnridePhotos.getInstance().getDataFolder(), layout.getBackgroundImage() + ".png");
                    canvas = ImageIO.read(imageFile);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                Graphics graphics = canvas.createGraphics();

                for(int i = 0; i < faces.length; i++)
                {
                    Face face = faces[i];
                    FaceLayout faceLayout = layout.getFaceLayout(i);
                    if(faceLayout == null)
                        continue;

                    BufferedImage faceImage = downloadFace(face, faceLayout);

                    graphics.drawImage(faceImage, faceLayout.getX(), faceLayout.getY(), faceLayout.getWidth(), faceLayout.getHeight(), null);
                }

                try {
                    File imageFile = new File(OnridePhotos.getInstance().getDataFolder(), layout.getForegroundImage() + ".png");
                    graphics.drawImage(ImageIO.read(imageFile), 0, 0, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                graphics.dispose();
                result = canvas;
                callback.runTaskLater(OnridePhotos.getInstance(), 1);
            }
        };

        downloadRunnable.runTaskLaterAsynchronously(OnridePhotos.getInstance(), 1);
    }

    public BufferedImage getResult()
    {
        if(result == null)
        {
            Bukkit.getLogger().severe("You have to call render image, wait for it to finish. only then you can use getResult(). Its recommended to use the whenDone(BukkitRunnable) function for this! Check documentation!");
            return null;
        }
        return result;
    }

    private BufferedImage downloadFace(Face face, FaceLayout layout)
    {
        try {
            String imageUrl = layout.getWebEndpointForFace(face);
            URL url = new URL(imageUrl);

            return ImageIO.read(url);
        } catch (Exception e)
        {
            Bukkit.getLogger().warning("[Onride Photos] Could not download face for player: " + face.getPlayerUUID() + "because:\n" + e);
        }

        return null;
    }

    public ItemStack getResultAsMapItem(World world)
    {
        BufferedImage result = getResult();
        if(result == null)
        {
            Bukkit.getLogger().severe("The above is also the case for getResultAsMapItem!!!");
            return null;
        }

        MapView mapView = Bukkit.createMap(world);
        mapView.addRenderer(new MapRenderer() {
            private boolean hasRendered = false;

            @Override
            public void render(MapView view, MapCanvas canvas, Player player) {
                if (hasRendered) return;
                hasRendered = true;

                for (int x = 0; x < 128; x++) {
                    for (int y = 0; y < 128; y++) {
                        int rgb = result.getRGB(x, y);
                        byte color = MapPalette.matchColor(new Color(rgb, true));
                        canvas.setPixel(x, y, color);
                    }
                }
            }
        });

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        meta.setMapView(mapView);
        mapItem.setItemMeta(meta);

        return mapItem;
    }
}

