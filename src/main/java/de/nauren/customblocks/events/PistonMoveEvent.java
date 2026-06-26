package de.nauren.customblocks.events;

import de.nauren.customblocks.util.CustomBlockManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonMoveEvent implements Listener {

    private final CustomBlockManager customBlockManager;

    public PistonMoveEvent(CustomBlockManager customBlockManager) {
        this.customBlockManager = customBlockManager;
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            moveCustomBlock(block, event.getDirection());
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            moveCustomBlock(block, event.getDirection());
        }
    }

    private void moveCustomBlock(Block block, BlockFace direction) {
        customBlockManager.moveCustomBlock(block, block.getRelative(direction));
    }
}
