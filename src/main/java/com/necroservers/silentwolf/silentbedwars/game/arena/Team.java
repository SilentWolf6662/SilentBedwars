package com.necroservers.silentwolf.silentbedwars.game.arena;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Team {
    private final String color;
    private final String colorCode; // §c, §9, etc.
    private final List<Player> players = new ArrayList<>();
    private final Level level;
    private final Vector3 spawn;
    private final double spawnYaw;
    private final Vector3 bed;
    private final Vector3 generator;
    private boolean bedAlive = true;

    public Team(String color, String colorCode, Level level, double sx, double sy, double sz, double bx, double by, double bz, double gx, double gy, double gz) {
        this.color = color;
        this.colorCode = colorCode;
        this.level = level;
        this.spawn = new Vector3(sx, sy, sz);
        this.spawnYaw = 0; // default yaw, can be customized
        this.bed = new Vector3(bx, by, bz);
        this.generator = new Vector3(gx, gy, gz);
    }

    public void addPlayer(Player player) {
        players.add(player);
        Location waitingLobby = new Location(49.71, 5.00, -39.56, level);
        int chunkX = waitingLobby.getFloorX() >> 4;
        int chunkZ = waitingLobby.getFloorZ() >> 4;
        level.loadChunk(chunkX, chunkZ);
        player.teleport(waitingLobby);
        player.getInventory().clearAll();
        player.sendMessage("§aJoined " + color + " Team!");
    }

    public void teleportPlayersToSpawn() {
        for (Player p : players) {
            p.getInventory().clearAll();
            int chunkX = spawn.getFloorX() >> 4;
            int chunkZ = spawn.getFloorZ() >> 4;
            level.loadChunk(chunkX, chunkZ);  // force load
            p.teleport(spawn);
            p.setGamemode(Player.SURVIVAL);
        }
    }

    public void onBedDestroyed() {
        this.bedAlive = false;
        for (Player p : players) {
            p.sendMessage("§cYour bed has been destroyed!");
        }
    }

    public boolean isEliminated() {
        return !bedAlive && players.stream().allMatch(Player::isSpectator);
    }

    public void eliminatePlayer(@NotNull Player player) {
        player.setGamemode(Player.SPECTATOR);
        player.sendMessage("§cYou are eliminated!");
    }

    public void reset() {
        for (Player p : players) {
            p.setGamemode(Player.ADVENTURE);
            p.getInventory().clearAll();
            p.teleport(new Location(35.19, 6.00, -46.95, level)); // lobby
        }
        players.clear();
        bedAlive = true;
    }

}
