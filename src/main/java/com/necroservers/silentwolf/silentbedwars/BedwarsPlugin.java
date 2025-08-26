package com.necroservers.silentwolf.silentbedwars;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.necroservers.silentwolf.silentbedwars.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.command.BedwarsCommand;
import com.necroservers.silentwolf.silentbedwars.listener.GameListener;
import lombok.Getter;

public class BedwarsPlugin extends PluginBase {

    @Getter
    private static BedwarsPlugin instance;
    @Getter
    private Arena arena;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        instance = this;

        // Load test arena
        arena = new Arena("test", getServer().getLevelByName("world"));
        arena.loadDefaultTeams();

        // Register command & events
        getServer().getCommandMap().register("bedwars", new BedwarsCommand(arena));
        getServer().getPluginManager().registerEvents(new GameListener(arena), this);

        getLogger().info(TextFormat.GREEN + "SilentBedwars enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.YELLOW + "SilentBedwars disabled.");
    }

    public static void log(String message) {
        getInstance().getLogger().info(message);
    }
}