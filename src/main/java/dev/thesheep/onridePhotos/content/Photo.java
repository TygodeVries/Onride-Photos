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
import java.net.HttpURLConnection;
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

                /*
                        Add Background Image
                 */
                try {
                    File imageFile = new File(OnridePhotos.getInstance().getDataFolder(), layout.getBackgroundImage() + ".png");
                    if(!imageFile.exists()) {
                        OnridePhotos.getInstance().getLogger().severe("Could not find the background image to create photo! Was expecting a background picture at " + layout.getBackgroundImage() + ".png");
                    }
                    else {
                        canvas = ImageIO.read(imageFile);
                    }
                } catch (Exception e) {
                    OnridePhotos.getInstance().getLogger().severe("Failed to add background image onto photo because: " + e);
                }


                 /*
                        Add faces
                 */

                Graphics graphics = canvas.createGraphics();

                for(int i = 0; i < faces.length; i++) {
                    try {
                        Face face = faces[i];
                        FaceLayout faceLayout = layout.getFaceLayout(i);
                        if (faceLayout == null)
                        {
                            OnridePhotos.getInstance().getLogger().warning("Could not fit all riders onto photo, skipped rider " + i);
                            continue;
                        }

                        BufferedImage faceImage = downloadFace(face, faceLayout);
                        if(faceImage == null) // in case it failed to load
                        {
                            OnridePhotos.getInstance().getLogger().warning("Removed a rider from the picture, because it failed to load.");
                            continue;
                        }

                        graphics.drawImage(faceImage, faceLayout.getX(), faceLayout.getY(), faceLayout.getWidth(), faceLayout.getHeight(), null);
                    }
                    catch (Exception e)
                    {
                        OnridePhotos.getInstance().getLogger().severe("Could not add face to a photo! because: " + e);
                    }
                }

                 /*
                        Add Foreground Image
                 */

                try {
                    File imageFile = new File(OnridePhotos.getInstance().getDataFolder(), layout.getForegroundImage() + ".png");
                    if(!imageFile.exists()) {
                        OnridePhotos.getInstance().getLogger().severe("Could not find the foreground image to create photo! Was expecting a foreground picture at " + layout.getForegroundImage() + ".png");
                    }
                    else {
                        graphics.drawImage(ImageIO.read(imageFile), 0, 0, null);
                    }
                } catch (Exception e) {
                    OnridePhotos.getInstance().getLogger().severe("Failed to add foreground image onto photo because: " + e);
                }


                graphics.dispose();
                result = canvas;

                // Let everyone know we are done!
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

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");

            BufferedImage image = ImageIO.read(connection.getInputStream());
            connection.disconnect();
            return image;
        } catch (Exception e)
        {
            OnridePhotos.getInstance().getLogger().severe("Could not download face for player: " + face.getPlayerUUID() + " from the internet because:\n" + e);
        }

        return null;
    }

    public ItemStack getResultAsMapItem(World world)
    {
        BufferedImage result = getResult();
        if(result == null)
        {
            Bukkit.getLogger().severe("No result was returned, so the map is also not created.");
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

