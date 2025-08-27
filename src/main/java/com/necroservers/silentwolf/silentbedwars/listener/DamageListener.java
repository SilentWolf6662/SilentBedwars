package com.necroservers.silentwolf.silentbedwars.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityTnt;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.SpawnPointType;
import com.necroservers.silentwolf.silentbedwars.BedwarsPlugin;
import com.necroservers.silentwolf.silentbedwars.game.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.game.arena.GameState;
import com.necroservers.silentwolf.silentbedwars.game.arena.Team;
import com.necroservers.silentwolf.silentbedwars.game.damage.DamageCauseType;
import com.necroservers.silentwolf.silentbedwars.game.damage.LastHitInfo;
import com.necroservers.silentwolf.silentbedwars.util.TeamColor;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DamageListener implements Listener {

    private final Arena arena;

    public DamageListener(Arena arena) {
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team team = arena.getTeamByPlayer(player);

        if (arena.getState() != GameState.RUNNING || team == null) return;

        LastHitInfo lastHit = arena.getLastHit(player);
        String killMessage = buildKillMessage(player, lastHit, !team.isBedAlive());

        // Broadcast kill message
        for (Team t : arena.getTeams()) {
            for (Player p : t.getPlayers()) {
                p.sendMessage(killMessage);
            }
        }

        if (!team.isBedAlive()) {
            // Mark elimination

            team.eliminatePlayer(player);
            arena.checkWinCondition();
        }

        // If bed is alive, player will insta respawn
        // schedule the respawn and teleport one tick later
        player.getServer().getScheduler().scheduleDelayedTask(() -> {
            // teleport to spawn or set gamemode
            Vector3 spawnCoords = team.getSpawn();
            Position spawnPos = new Position(spawnCoords.x, spawnCoords.y, spawnCoords.z, arena.getLevel());
            spawnPos.getLevel().loadChunk(spawnPos.getFloorX() >> 4, spawnPos.getFloorZ() >> 4);
            double x = team.getSpawn().x;
            double y = team.getSpawn().y;
            double z = team.getSpawn().z;

            Position spawn = new Position(x, y, z, arena.getLevel());
            spawn.setStrong();
            player.teleport(spawn);
            player.setGamemode(Player.SURVIVAL);
        }, 20 * 5); // 1 tick later
    }


    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Team team = arena.getTeamByPlayer(player);

        if (arena.getState() != GameState.RUNNING || team == null) {
            // fallback to lobby
            event.setRespawnPosition(Pair.of(
                    new Position(35.19, 6.00, -46.95, BedwarsPlugin.getInstance().getServer().getDefaultLevel()),
                    SpawnPointType.PLAYER
            ));
            player.setGamemode(Player.SURVIVAL);
            return;
        }

        if (team.isBedAlive()) {
            Vector3 spawnVec = team.getSpawn();
            Position spawn = new Position(spawnVec.x, spawnVec.y, spawnVec.z, arena.getLevel());
            spawn.getLevel().loadChunk(spawn.getFloorX() >> 4, spawn.getFloorZ() >> 4);

            event.setRespawnPosition(Pair.of(spawn, SpawnPointType.PLAYER));
            player.setGamemode(Player.SURVIVAL);
            player.sendMessage("§aRespawning at your team's spawn!");
        } else {
            // Spectator mode
            Position spectatorPos = new Position(49.71, 100, -39.56, arena.getLevel());
            event.setRespawnPosition(Pair.of(spectatorPos, SpawnPointType.PLAYER));
            player.setGamemode(Player.SPECTATOR);
            player.sendMessage("§cYou are eliminated and in spectator mode.");
        }
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        if (event.getDamager() instanceof Player damager) {
            arena.recordHit(victim, damager.getUniqueId(), DamageCauseType.MELEE);
        } else if (event.getDamager() instanceof EntityProjectile projectile) {
            if (projectile.shootingEntity instanceof Player shooter) {
                arena.recordHit(victim, shooter.getUniqueId(), DamageCauseType.PROJECTILE);
            }
        } else if (event.getDamager() instanceof EntityTnt tnt &&
                tnt.getSource() instanceof Player source) {
            arena.recordHit(victim, source.getUniqueId(), DamageCauseType.TNT);
        }
    }

    @EventHandler
    public void onVoidDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim &&
                event.getCause() == EntityDamageEvent.DamageCause.VOID) {

            LastHitInfo info = arena.getLastHit(victim);
            if (info != null && info.getDamager() != null) {
                arena.recordHit(victim, info.getDamager(), DamageCauseType.VOID);
            }
        }
    }

    private @NotNull String buildKillMessage(Player victim, LastHitInfo info, boolean eliminated) {
        Team victimTeam = arena.getTeamByPlayer(victim);

        if (info == null || info.getDamager() == null) {
            if (eliminated) {
                return victimTeam.getColorCode() + " " + victim.getName() + " §7was eliminated.";
            }
            return victimTeam.getColor() + victim.getName() + " §7died.";
        }

        UUID killerId = info.getDamager();
        String killerColor = TeamColor.getTeamColorCode(arena.getTeamByPlayer(BedwarsPlugin.getInstance().getServer().getPlayer(killerId).orElse(null)));

        String killerName = arena.getPlayerName(killerId);

        if (eliminated) {
            return victimTeam.getColorCode() + " " + victim.getName() + " §7was eliminated by " + killerColor + killerName;
        }

        return switch (info.getCause()) {
            case MELEE -> victimTeam.getColorCode() + " " + victim.getName() + " §7was slain by " + killerColor + killerName;
            case PROJECTILE -> victimTeam.getColorCode() + " " + victim.getName() + " §7was shot by " + killerColor + killerName;
            case TNT -> victimTeam.getColorCode() + " " + victim.getName() + " §7was blown up by " + killerColor + killerName;
            case VOID -> victimTeam.getColorCode() + " " + victim.getName() + " §7was knocked into the void by " + killerColor + killerName;
            default -> victimTeam.getColorCode() + " " + victim.getName() + " §7died.";
        };
    }
}