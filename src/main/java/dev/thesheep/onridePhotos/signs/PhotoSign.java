package dev.thesheep.onridePhotos.signs;

import dev.thesheep.onridePhotos.display.PhotoDisplay;
import dev.thesheep.onridePhotos.utils.SerializableLocation;
import dev.thesheep.onridePhotos.utils.SerializableVector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PhotoSign implements Listener {
    @EventHandler
    public void on(SignChangeEvent event)
    {
        String header = event.getLine(0);
        if(!header.equalsIgnoreCase("[display]"))
        {
            return;
        }

        String id = event.getLine(1);
        Block block = event.getBlock();
        World world = block.getWorld();

        BlockFace facing = null;
        if (block.getBlockData() instanceof Directional) {
            facing = ((Directional) block.getBlockData()).getFacing();
        }

        if (facing == null) {
            return;
        }

        Location itemFrameLocation = block.getLocation();

        ItemFrame itemFrame = (ItemFrame) world.spawnEntity(itemFrameLocation, EntityType.ITEM_FRAME);

        // Make the item frame face back toward the sign
        itemFrame.setFacingDirection(facing, true);
        itemFrame.setItem(new ItemStack(Material.MAP));

        BlockFace[] adjacentDirections = new BlockFace[] {
                getClockwise(facing),
                getCounterClockwise(facing),
                BlockFace.UP,
                BlockFace.DOWN
        };

        Vector pushDirection = null;  // Initialize to null

        BlockFace[] adjacentDirectionsNew = new BlockFace[] {
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
        };

        for (BlockFace dir : adjacentDirectionsNew) {
            Block relativeBlock = itemFrame.getLocation().getBlock().getRelative(dir);
            for (Entity entity : itemFrame.getWorld().getNearbyEntities(relativeBlock.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5)) {
                if (entity instanceof ItemFrame) {
                    ItemFrame otherFrame = (ItemFrame) entity;
                    if (otherFrame.getFacing() == facing) {
                        System.out.println("Found adjacent item frame at direction: " + dir);
                        // Set pushDirection as vector of dir
                        pushDirection = new Vector(dir.getModX(), dir.getModY(), dir.getModZ());
                        break; // Stop searching once found
                    }
                }
            }
            if (pushDirection != null) break;
        }

        event.getBlock().setType(Material.AIR);

        if (pushDirection == null) {
            pushDirection = new Vector(0, 0, 0);
        }

        PhotoDisplay photoDisplay = new PhotoDisplay(new SerializableLocation(itemFrame.getLocation().getBlock().getLocation()), id, new SerializableVector(pushDirection));
        PhotoDisplay.displayList.add(photoDisplay);
        PhotoDisplay.saveAll();
    }


    public BlockFace getClockwise(BlockFace face) {
        switch(face) {
            case NORTH: return BlockFace.EAST;
            case EAST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.WEST;
            case WEST: return BlockFace.NORTH;
            default: return face; // For UP, DOWN, or others just return same
        }
    }

    public BlockFace getCounterClockwise(BlockFace face) {
        switch(face) {
            case NORTH: return BlockFace.WEST;
            case WEST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.EAST;
            case EAST: return BlockFace.NORTH;
            default: return face;
        }
    }
}

