package dev.thesheep.onridePhotos.display;

import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.utils.FancyParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DisplaySelector {
    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    applySelectionEffect(player);
                }
            }
        }.runTaskTimer(OnridePhotos.getInstance(), 5, 5);
    }

    private static void applySelectionEffect(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!PhotoPass.isPhotoPass(item))
            return;

        ItemFrame display = PhotoDisplay.raytracePhotoDisplay(player);
        if(display == null)
            return;

        Location location = display.getLocation();

        FancyParticles.drawParticleCube(location.getBlock().getLocation(), player);
    }


}
