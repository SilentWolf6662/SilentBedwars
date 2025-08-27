package com.necroservers.silentwolf.silentbedwars.game.arena;

import cn.nukkit.IPlayer;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import com.necroservers.silentwolf.silentbedwars.BedwarsPlugin;
import com.necroservers.silentwolf.silentbedwars.game.generator.Generator;
import com.necroservers.silentwolf.silentbedwars.game.generator.GeneratorManager;
import com.necroservers.silentwolf.silentbedwars.game.generator.ItemGenerator;
import com.necroservers.silentwolf.silentbedwars.game.generator.TeamGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.necroservers.silentwolf.silentbedwars.game.damage.*;
import org.jetbrains.annotations.NotNull;

@Getter
public class Arena {
    private final String name;
    private final Level level;
    private final Map<String, Team> teams = new HashMap<>();
    @Setter @Getter
    private GameState state = GameState.WAITING;
    private final GeneratorManager generatorManager = new GeneratorManager();
    private int countdownTaskId = -1;

    private final Map<UUID, LastHitInfo> lastHits = new ConcurrentHashMap<>();

    public Arena(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    public void loadDefaultTeams() {
        teams.put("red", new Team("Red", "§c", level, 65.5, 5, -43.5, 65, 5, -49, 65.5, 5, -43.5));  // spawn, bed
        teams.put("blue", new Team("Blue", "§9", level, 65.5, 5, -58.5, 65, 5, -52, 65.5, 5, -58.5));
    }

    public void addPlayer(Player player) {
        teams.values().stream()
                .min(Comparator.comparingInt(t -> t.getPlayers().size())).ifPresent(smallest -> smallest.addPlayer(player));
        // Check if enough players to start
        if (state == GameState.WAITING && getPlayerCount() >= 2) {
            startCountdown();
        }
    }

    public void startCountdown() {
        state = GameState.STARTING;
        int[] timeLeft = {5}; // 10-second countdown

        countdownTaskId = BedwarsPlugin.getInstance().getServer().getScheduler()
                .scheduleRepeatingTask(BedwarsPlugin.getInstance(), () -> {
                    if (timeLeft[0] <= 0) {
                        BedwarsPlugin.getInstance().getServer().getScheduler().cancelTask(countdownTaskId);
                        startGame();
                        return;
                    }

                    broadcast("§eGame starts in " + timeLeft[0] + "...");
                    timeLeft[0]--;
                }, 20, true).getTaskId(); // run every 20 ticks (1 second)
    }

    private void startGame() {
        state = GameState.RUNNING;
        broadcast("§aGame has started!");
        for (Team t : teams.values()) {
            t.teleportPlayersToSpawn();
        }
        // TODO: enable generators, etc.

        generatorSetup();

        BedwarsPlugin.getInstance().getServer().getScheduler().scheduleRepeatingTask(BedwarsPlugin.getInstance(), () -> {
            generatorManager.tickAll(Server.getInstance().getTick());
        }, 1);
    }

    private void generatorSetup() {
        Generator gen = new ItemGenerator("diamond", level, new Vector3(70.48, 5.00, -48.51), 20 * 5, Item.DIAMOND, 5);
        Generator gen1 = new ItemGenerator("diamond2", level, new Vector3(60.71, 5.00, -51.48), 20 * 5, Item.DIAMOND, 5);
        Generator gen2 = new ItemGenerator("emerald", level, new Vector3(60.55, 5.00, -46.49), 20 * 10, Item.EMERALD, 2);
        Generator gen3 = new ItemGenerator("emerald2", level, new Vector3(70.20, 5.00, -55.48), 20 * 10, Item.EMERALD, 2);

        generatorManager.addGenerator(gen);
        generatorManager.addGenerator(gen1);
        generatorManager.addGenerator(gen2);
        generatorManager.addGenerator(gen3);

        for (Team t : teams.values()) {
            Vector3 genCoords = t.getGenerator();
            TeamGenerator teamGen = new TeamGenerator(t.getColor().toLowerCase(), level, genCoords);
            teamGen.addItem(Item.get(Item.IRON_INGOT, 0, 1), 20, 30); // every second
            teamGen.addItem(Item.get(Item.GOLD_INGOT, 0, 1), 60, 20); // every 3 seconds
            generatorManager.addGenerator(teamGen);
        }
    }

    public void broadcast(String message) {
        for (Team t : teams.values()) {
            for (Player p : t.getPlayers()) {
                p.sendMessage(message);
            }
        }
    }

    public void checkWinCondition() {
        List<Team> aliveTeams = new ArrayList<>();
        for (Team team : teams.values()) {
            if (!team.isEliminated()) aliveTeams.add(team);
        }

        if (aliveTeams.size() == 1) {
            Team winner = aliveTeams.getFirst();
            for (Player p : level.getPlayers().values()) {
                p.sendMessage("§a" + winner.getColor() + " Team has won the game!");
            }
            reset();
        }
    }

    public void reset() {
        state = GameState.WAITING;
        lastHits.clear();
        generatorManager.clear();

        for (Team t : teams.values()) {
            t.reset();
        }
    }

    public Team getTeamByPlayer(Player player) {
        return teams.values().stream().filter(t -> t.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public int getPlayerCount() {
        return teams.values().stream().mapToInt(t -> t.getPlayers().size()).sum();
    }

    public void recordHit(@NotNull Player victim, UUID damagerId, DamageCauseType cause) {
        lastHits.put(victim.getUniqueId(), new LastHitInfo(
                damagerId,
                cause,
                System.currentTimeMillis()
        ));
    }

    public LastHitInfo getLastHit(@NotNull Player victim) {
        LastHitInfo info = lastHits.get(victim.getUniqueId());
        if (info == null) return null;
        if (System.currentTimeMillis() - info.getTimestamp() > 10_000) return null; // expire after 10s
        return info;
    }

    public String getPlayerName(UUID uuid) {
        // First try online player
        Player online = BedwarsPlugin.getInstance().getServer().getPlayer(uuid).orElse(null);
        if (online != null) {
            return online.getName();
        }

        // Fallback: Nukkit's offline player cache
        return BedwarsPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    public IPlayer getPlayerOffline(UUID uuid) {
        // First try online player
        Player online = BedwarsPlugin.getInstance().getServer().getPlayer(uuid).orElse(null);
        if (online != null) {
            return online;
        }

        // Fallback: Nukkit's offline player cache
        return BedwarsPlugin.getInstance().getServer().getOfflinePlayer(uuid);
    }
}
