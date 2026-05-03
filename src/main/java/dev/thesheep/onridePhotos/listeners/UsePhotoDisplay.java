package dev.thesheep.onridePhotos.listeners;

import dev.thesheep.onridePhotos.display.PhotoDisplay;
import dev.thesheep.onridePhotos.display.PhotoPass;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UsePhotoDisplay implements Listener {
    @EventHandler
    public void on(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            use(event.getPlayer(), event);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event)
    {
        use(event.getPlayer(), event);
    }

    public void use(Player player, Cancellable event) {

        boolean isPhotoPass = false;
        boolean isDisplay = false;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (PhotoPass.isPhotoPass(item)) {
            isPhotoPass = true;
        }

        ItemFrame frame = PhotoDisplay.raytracePhotoDisplay(player);
        if (frame != null) {
            isDisplay = true;
        }

        if (isPhotoPass || isDisplay) {
            event.setCancelled(true);
        }

        if (isDisplay && !isPhotoPass) {
            player.sendMessage("§cYou have to use a photo pass to buy this photo!");
            return;
        }

        if (!isDisplay && isPhotoPass) {
            player.sendMessage("§cYou have to select a display to buy the photo from!");
            return;
        }

        if (isDisplay && isPhotoPass) {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().addItem(frame.getItem().clone());
            player.sendMessage("§aYou bought a photo!");
        }
    }
}
