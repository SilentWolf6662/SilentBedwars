package com.necroservers.silentwolf.silentbedwars.util;

import com.necroservers.silentwolf.silentbedwars.arena.Team;
import org.jetbrains.annotations.NotNull;

public class TeamColor {
    public static @NotNull String getTeamColorCode(Team team) {
        if (team == null) return "§7";
        return switch (team.getColor().toLowerCase()) {
            case "red" -> "§c";
            case "blue" -> "§9";
            case "green" -> "§a";
            case "yellow" -> "§e";
            case "pink" -> "§d";
            case "purple" -> "§5";
            case "orange" -> "§6";
            case "white" -> "§f";
            case "black" -> "§0";
            case "gray", "grey" -> "§8";
            case "light blue", "aqua", "cyan" -> "§b";
            case "lime" -> "§2";
            case "brown" -> "§4";
            default -> "§7";
        };
    }
}
