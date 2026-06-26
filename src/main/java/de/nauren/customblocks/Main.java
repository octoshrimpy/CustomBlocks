package de.nauren.customblocks;

import de.nauren.customblocks.events.BreakEvent;
import de.nauren.customblocks.events.PistonMoveEvent;
import de.nauren.customblocks.events.PlaceEvent;
import de.nauren.customblocks.events.ResourcePackStatus;
import de.nauren.customblocks.util.CommandManager;
import de.nauren.customblocks.util.CustomBlockManager;
import de.nauren.customblocks.util.FileManager;
import de.nauren.customblocks.util.RegisterRecipes;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private FileManager fileManager;
    private CustomBlockManager customBlockManager;

    @Override
    public void onEnable() {
        fileManager = new FileManager(this);
        customBlockManager = new CustomBlockManager(this, fileManager);

        getServer().getPluginManager().registerEvents(new PlaceEvent(customBlockManager), this);
        getServer().getPluginManager().registerEvents(new BreakEvent(fileManager, customBlockManager), this);
        getServer().getPluginManager().registerEvents(new PistonMoveEvent(customBlockManager), this);
        getServer().getPluginManager().registerEvents(new ResourcePackStatus(fileManager), this);

        RegisterRecipes.RegisterStonecutterRecipes(fileManager, this);

        CommandManager commandManager = new CommandManager(fileManager);
        Objects.requireNonNull(this.getCommand("cb")).setExecutor(commandManager);
        Objects.requireNonNull(this.getCommand("cb")).setTabCompleter(commandManager);

        customBlockManager.rebuildIndex();
    }

    @Override
    public void onDisable() {
        if (customBlockManager != null) {
            customBlockManager.clearIndex();
        }
    }
}
