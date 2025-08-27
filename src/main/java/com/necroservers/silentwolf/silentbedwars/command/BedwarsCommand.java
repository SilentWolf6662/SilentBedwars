package com.necroservers.silentwolf.silentbedwars.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import com.necroservers.silentwolf.silentbedwars.game.arena.Arena;

public class BedwarsCommand extends Command {

    private final Arena arena;

    public BedwarsCommand(Arena arena) {
        super("bw", "Join Bedwars Arena");
        this.arena = arena;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        arena.addPlayer(p);
        return true;
    }
}
