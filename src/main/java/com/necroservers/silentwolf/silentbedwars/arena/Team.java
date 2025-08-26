package com.necroservers.silentwolf.silentbedwars.arena;

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
    private final String name;
    private final List<Player> players = new ArrayList<>();
    private final Level level;
    private final Vector3 spawn;
    private final Vector3 bed;
    private boolean bedAlive = true;

    public Team(String name, Level level, double sx, double sy, double sz, double bx, double by, double bz) {
        this.name = name;
        this.level = level;
        this.spawn = new Vector3(sx, sy, sz);
        this.bed = new Vector3(bx, by, bz);
    }

    public void addPlayer(Player player) {
        players.add(player);
        Location waitingLobby = new Location(53.38, 5.00, -50.48, level);
        int chunkX = waitingLobby.getFloorX() >> 4;
        int chunkZ = waitingLobby.getFloorZ() >> 4;
        level.loadChunk(chunkX, chunkZ);
        player.teleport(waitingLobby);
        player.sendMessage("§aJoined " + name + " Team!");
    }

    public void teleportPlayersToSpawn() {
        for (Player p : players) {
            int chunkX = spawn.getFloorX() >> 4;
            int chunkZ = spawn.getFloorZ() >> 4;
            level.loadChunk(chunkX, chunkZ);  // force load
            p.teleport(spawn);
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
        players.clear();
        bedAlive = true;
    }

}
