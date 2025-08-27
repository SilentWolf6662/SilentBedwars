package com.necroservers.silentwolf.silentbedwars.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.necroservers.silentwolf.silentbedwars.BedwarsPlugin;
import com.necroservers.silentwolf.silentbedwars.game.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.game.arena.GameState;

public class ForceEndCommand extends Command {

    public ForceEndCommand() {
        super("forceend", "Force end a Bedwars game", "/forceend <arena>", new String[]{"fe"});
        this.setPermission("silentbedwars.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§eUsage: /forceend <arena>");
            return true;
        }

        Arena arena = BedwarsPlugin.getInstance().getArena();
        if (arena == null) {
            sender.sendMessage("§cArena not found: " + args[0]);
            return true;
        }

        if (arena.getState() != GameState.RUNNING && arena.getState() != GameState.STARTING) {
            sender.sendMessage("§cArena " + args[0] + " is not running!");
            return true;
        }

        arena.broadcast("§cThe game has been force-ended by an admin.");
        arena.reset();
        sender.sendMessage("§aArena " + args[0] + " has been force-ended!");
        return true;
    }
}
