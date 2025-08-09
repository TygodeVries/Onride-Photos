package dev.thesheep.onridePhotos.commands;

import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.content.Photo;
import dev.thesheep.onridePhotos.content.Face;
import dev.thesheep.onridePhotos.content.PhotoLayout;
import dev.thesheep.onridePhotos.display.PhotoDisplay;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class PhotoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(!commandSender.isOp())
        {
            commandSender.sendMessage("§cYou do not have permission todo this.");
            return true;
        }

        Player player = (Player) commandSender;

        if(strings.length == 0)
        {
            commandSender.sendMessage("§cIncorrect Usage! do /photo [layout] [player] [player] [player]...");
            return true;
        }

        String layoutFile = strings[0];
        File file = new File(OnridePhotos.getInstance().getDataFolder() + "/layout/" + layoutFile + ".json");
        PhotoLayout layout = PhotoLayout.loadFromFile(file);
        Face[] faces = new Face[strings.length - 1];

        for(int i = 1; i < strings.length; i++)
        {
            OfflinePlayer selected = Bukkit.getOfflinePlayer(strings[i]);
            faces[i - 1] = new Face(selected.getUniqueId());
        }

        Photo photo = new Photo(layout, faces);
        photo.renderImage();
        photo.whenDone(new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack result = photo.getResultAsMapItem(player.getWorld());
                player.getInventory().addItem(result);
            }
        });

        return true;
    }
}
