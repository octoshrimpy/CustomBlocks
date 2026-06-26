package de.nauren.customblocks.events;

import de.nauren.customblocks.util.CustomBlockManager;
import de.nauren.customblocks.util.FileManager;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BreakEvent implements Listener {
    private final FileManager fileManager;
    private final CustomBlockManager customBlockManager;

    public BreakEvent(FileManager fileManager, CustomBlockManager customBlockManager) {
        this.fileManager = fileManager;
        this.customBlockManager = customBlockManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockBreak(event.getBlock(), event.getPlayer().getGameMode(), event.isDropItems(), event);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        handleBlockBreak(event.getBlock(), null, false, null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            handleBlockBreak(block, null, false, null);
        }
    }

    private void handleBlockBreak(Block block, GameMode gameMode, boolean shouldDropItems, BlockBreakEvent event) {
        boolean removed = customBlockManager.removeCustomBlock(block, gameMode == GameMode.SURVIVAL && shouldDropItems, fileManager);
        if (removed && event != null) {
            event.setDropItems(false);
        }
    }
}
