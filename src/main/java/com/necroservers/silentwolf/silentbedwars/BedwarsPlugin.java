package com.necroservers.silentwolf.silentbedwars;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.necroservers.silentwolf.silentbedwars.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.command.BedwarsCommand;
import com.necroservers.silentwolf.silentbedwars.command.ForceEndCommand;
import com.necroservers.silentwolf.silentbedwars.command.ForceStartCommand;
import com.necroservers.silentwolf.silentbedwars.listener.BlockBreakListener;
import com.necroservers.silentwolf.silentbedwars.listener.DamageListener;
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
        this.getServer().getCommandMap().register("bedwars", new BedwarsCommand(arena));
        this.getServer().getCommandMap().register("forcestart", new ForceStartCommand());
        this.getServer().getCommandMap().register("forceend", new ForceEndCommand());
        this.getServer().getPluginManager().registerEvents(new DamageListener(arena), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(arena), this);

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