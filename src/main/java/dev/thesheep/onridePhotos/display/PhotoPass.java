package dev.thesheep.onridePhotos.display;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PhotoPass {
    public static boolean isPhotoPass(ItemStack item)
    {
        if(item.getType() != Material.PAPER)
            return  false;

        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return  false;

        if(!meta.getDisplayName().contains("Photo Pass"))
            return false;

        return true;
    }
}
