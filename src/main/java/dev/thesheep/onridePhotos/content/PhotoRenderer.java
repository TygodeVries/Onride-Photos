package dev.thesheep.onridePhotos.content;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PhotoRenderer extends MapRenderer {
    private final BufferedImage image;
    private boolean rendered = false;

    public PhotoRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (rendered) return;
        rendered = true;

        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                int rgb = image.getRGB(x, y);
                canvas.setPixel(x, y, MapPalette.matchColor(new Color(rgb, true)));
            }
        }
    }

}
