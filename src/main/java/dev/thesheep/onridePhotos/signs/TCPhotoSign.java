package dev.thesheep.onridePhotos.signs;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.content.Face;
import dev.thesheep.onridePhotos.content.Photo;
import dev.thesheep.onridePhotos.content.PhotoLayout;
import dev.thesheep.onridePhotos.database.ImageMetadata;
import dev.thesheep.onridePhotos.dependency.UUIDFixer;
import dev.thesheep.onridePhotos.display.PhotoDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class TCPhotoSign extends SignAction {@Override
public boolean match(SignActionEvent signActionEvent) {
    return signActionEvent.isType("photo");
}

    @Override
    public void execute(SignActionEvent info) {

        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && info.isPowered() && info.hasGroup())
        {
            for (MinecartMember<?> member : info.getGroup())
            {
                takePhoto(info, member);
            }
        }

    }

    public void takePhoto(SignActionEvent info, MinecartMember<?> member) {
        String layoutFile = (info.getLine(2));
        String display = (info.getLine(3));

        File file = new File(OnridePhotos.getInstance().getDataFolder() + "/layout/" + layoutFile + ".json");
        if(!file.exists())
        {
            OnridePhotos.getInstance().getLogger().severe("A photo sign tried to take a picture for layout " + layoutFile + " but the layout could not be found. It was expected to be at " + file.getAbsolutePath());
            return;
        }

        PhotoLayout layout = PhotoLayout.loadFromFile(file);
        if(layout == null)
            return;

        List<Player> players = member.getEntity().getPlayerPassengers();
        Face[] faces = new Face[players.size()];

        for (int i = 0; i < players.size(); i++) {
            faces[i] = new Face(UUIDFixer.getWrappedUUID(players.get(i)), players.get(i).getDisplayName());
        }

        Photo photo = new Photo(layout, faces);
        photo.renderImage();
        photo.whenDone(new BukkitRunnable() {
            @Override
            public void run() {

                ItemStack result = photo.getResultAsMapItem(info.getWorld());
                if(display.isBlank())
                {
                    info.getWorld().dropItem(info.getLocation(), result);
                }
                else {
                    PhotoDisplay.getById(display).addImage(result);
                }
            }
        });
    }


    @Override
    public boolean build(SignChangeActionEvent signChangeActionEvent) {
        return SignBuildOptions.create()
                .setName(signChangeActionEvent.isCartSign() ? "cart photo camera" : "train photo camera")
                .setDescription("take a photo of whoever is in the train")
                .handle(signChangeActionEvent.getPlayer());
    }
}
