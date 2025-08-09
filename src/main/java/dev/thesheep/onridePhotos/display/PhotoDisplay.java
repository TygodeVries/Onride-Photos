package dev.thesheep.onridePhotos.display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.thesheep.onridePhotos.OnridePhotos;
import dev.thesheep.onridePhotos.utils.SerializableLocation;
import dev.thesheep.onridePhotos.utils.SerializableVector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhotoDisplay {
    public SerializableLocation location;
    public String id;
    public SerializableVector pushDirection;

    public PhotoDisplay(SerializableLocation location, String id, SerializableVector pushDirection)
    {
        this.location = location;
        this.id = id;
        this.pushDirection = pushDirection;
    }

    public static PhotoDisplay getById(String id)
    {
        return displayList.stream().filter(e -> e.id.equalsIgnoreCase(id)).toList().get(0);
    }

    public static List<PhotoDisplay> displayList = new ArrayList<>();

    private static final File saveFile = new File(OnridePhotos.getInstance().getDataFolder(), "displays.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    public static void saveAll() {
        try (Writer writer = new FileWriter(saveFile)) {
            gson.toJson(displayList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        if (!saveFile.exists()) return;

        try (Reader reader = new FileReader(saveFile)) {
            Type listType = new TypeToken<List<PhotoDisplay>>() {}.getType();
            List<PhotoDisplay> loadedList = gson.fromJson(reader, listType);

            if (loadedList != null) {
                displayList = loadedList;
            } else {
                displayList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addImage(ItemStack itemStack) {
        Location currentLocation = location.toLocation();
        World world = currentLocation.getWorld();
        Vector pushVec = pushDirection.toVector();

        while (true) {
            Location checkLocation = currentLocation.clone().add(0.5, 0.5, 0.5);

            List<Entity> nearbyEntities = world.getNearbyEntities(checkLocation, 0.5, 0.5, 0.5).stream()
                    .filter(e -> e instanceof ItemFrame)
                    .toList();

            if (nearbyEntities.isEmpty()) {
                break;
            }

            ItemFrame frame = (ItemFrame) nearbyEntities.get(0);
            ItemStack existingItem = frame.getItem();

            if (existingItem == null || existingItem.getType() == Material.AIR) {
                frame.setItem(itemStack);
                break;
            } else {
                frame.setItem(itemStack);
                itemStack = existingItem;
                currentLocation.add(pushVec);
            }
        }
    }




}
