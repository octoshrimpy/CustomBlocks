package de.nauren.customblocks.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomBlockManager {

    private final JavaPlugin plugin;
    private final FileManager fileManager;
    private final NamespacedKey customBlockKey;
    private final Map<BlockKey, UUID> blockToDisplay = new HashMap<>();

    public CustomBlockManager(JavaPlugin plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.customBlockKey = new NamespacedKey(plugin, "custom_block_location");
    }

    public void placeCustomBlock(Block block, ItemStack sourceItem) {
        removeCustomBlock(block, false, fileManager);

        Location itemDisplayLocation = block.getLocation().add(0.5, 0.5, 0.5);
        applyFacing(block, itemDisplayLocation);

        ItemStack itemStack = sourceItem.clone();
        itemStack.setAmount(1);

        ItemDisplay itemDisplay = block.getWorld().spawn(itemDisplayLocation, ItemDisplay.class);
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setBrightness(new Display.Brightness(5, 10));
        itemDisplay.setInvulnerable(true);
        itemDisplay.setSilent(true);
        itemDisplay.setShadowStrength(0);
        itemDisplay.setShadowRadius(0);
        itemDisplay.setTransformation(new Transformation(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
                new Vector3f(1.002f, 1.002f, 1.002f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)
        ));
        itemDisplay.teleport(itemDisplayLocation);
        itemDisplay.setDisplayHeight(0.5f);

        BlockKey blockKey = BlockKey.fromBlock(block);
        itemDisplay.getPersistentDataContainer().set(customBlockKey, PersistentDataType.STRING, blockKey.asString());
        blockToDisplay.put(blockKey, itemDisplay.getUniqueId());
    }

    public boolean removeCustomBlock(Block block, boolean dropItem, FileManager fileManager) {
        BlockKey blockKey = BlockKey.fromBlock(block);
        UUID uuid = blockToDisplay.remove(blockKey);
        if (uuid == null) {
            return false;
        }

        Entity entity = Bukkit.getEntity(uuid);
        if (!(entity instanceof ItemDisplay itemDisplay)) {
            return false;
        }

        ItemStack displayedItem = itemDisplay.getItemStack();
        if (displayedItem != null) {
            ItemMeta meta = displayedItem.getItemMeta();
            if (meta != null && meta.hasCustomModelData()) {
                String itemName = fileManager.getItemName(meta.getCustomModelData(), displayedItem.getType());
                meta.setDisplayName(itemName);
                displayedItem.setItemMeta(meta);
            }

            if (dropItem && block.getType() == displayedItem.getType()) {
                block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), displayedItem);
            }
        }

        itemDisplay.remove();
        block.setType(Material.AIR);
        return true;
    }

    public void moveCustomBlock(Block from, Block to) {
        BlockKey fromKey = BlockKey.fromBlock(from);
        UUID uuid = blockToDisplay.remove(fromKey);
        if (uuid == null) {
            return;
        }

        Entity entity = Bukkit.getEntity(uuid);
        if (!(entity instanceof ItemDisplay itemDisplay)) {
            return;
        }

        Location targetLocation = to.getLocation().add(0.5, 0.5, 0.5);
        applyFacing(to, targetLocation);
        itemDisplay.teleport(targetLocation);

        BlockKey toKey = BlockKey.fromBlock(to);
        itemDisplay.getPersistentDataContainer().set(customBlockKey, PersistentDataType.STRING, toKey.asString());
        blockToDisplay.put(toKey, uuid);
    }

    public void rebuildIndex() {
        blockToDisplay.clear();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(ItemDisplay.class)) {
                PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
                String encodedLocation = dataContainer.get(customBlockKey, PersistentDataType.STRING);
                if (encodedLocation == null) {
                    continue;
                }

                BlockKey blockKey = BlockKey.fromString(encodedLocation);
                if (blockKey != null) {
                    blockToDisplay.put(blockKey, entity.getUniqueId());
                }
            }
        }
    }

    public void clearIndex() {
        blockToDisplay.clear();
    }

    private void applyFacing(Block block, Location location) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            location.setYaw(blockFaceToYaw(directional.getFacing()));
        } else {
            location.setYaw(0.0f);
        }
        location.setPitch(0.0f);
    }

    private float blockFaceToYaw(BlockFace face) {
        return switch (face) {
            case NORTH -> 180.0f;
            case EAST -> -90.0f;
            case WEST -> 90.0f;
            default -> 0.0f;
        };
    }
}
