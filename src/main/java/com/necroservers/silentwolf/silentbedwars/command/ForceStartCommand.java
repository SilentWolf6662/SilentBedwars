package com.necroservers.silentwolf.silentbedwars.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.necroservers.silentwolf.silentbedwars.BedwarsPlugin;
import com.necroservers.silentwolf.silentbedwars.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.arena.GameState;

public class ForceStartCommand extends Command {

    public ForceStartCommand() {
        super("forcestart", "Force start a Bedwars game", "/forcestart <arena>", new String[]{"fs"});
        this.setPermission("silentbedwars.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§eUsage: /forcestart <arena>");
            return true;
        }

        Arena arena = BedwarsPlugin.getInstance().getArena();
        if (arena == null) {
            sender.sendMessage("§cArena not found: " + args[0]);
            return true;
        }

        if (arena.getState() == GameState.RUNNING) {
            sender.sendMessage("§cArena " + args[0] + " is already running!");
            return true;
        }

        arena.startCountdown(); // force start immediately
        sender.sendMessage("§aArena " + args[0] + " has been force-started!");
        return true;
    }
}
