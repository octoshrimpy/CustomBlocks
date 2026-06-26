package de.nauren.customblocks.events;

import de.nauren.customblocks.util.CustomBlockManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceEvent implements Listener {

    private final CustomBlockManager customBlockManager;

    public PlaceEvent(CustomBlockManager customBlockManager) {
        this.customBlockManager = customBlockManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) {
            return;
        }

        customBlockManager.placeCustomBlock(event.getBlockPlaced(), item);
    }
}
