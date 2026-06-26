package de.nauren.customblocks.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public final class BlockKey {

    private final String worldName;
    private final int x;
    private final int y;
    private final int z;

    private BlockKey(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockKey fromBlock(Block block) {
        return new BlockKey(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public static BlockKey fromString(String encoded) {
        String[] parts = encoded.split(":", 4);
        if (parts.length != 4) {
            return null;
        }

        try {
            return new BlockKey(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public String asString() {
        return worldName + ":" + x + ":" + y + ":" + z;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BlockKey blockKey)) {
            return false;
        }
        return x == blockKey.x && y == blockKey.y && z == blockKey.z && Objects.equals(worldName, blockKey.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }
}
