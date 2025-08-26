package com.necroservers.silentwolf.silentbedwars.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.math.Vector3;
import com.necroservers.silentwolf.silentbedwars.BedwarsPlugin;
import com.necroservers.silentwolf.silentbedwars.arena.Arena;
import com.necroservers.silentwolf.silentbedwars.arena.GameState;
import com.necroservers.silentwolf.silentbedwars.arena.Team;
import org.jetbrains.annotations.NotNull;

public class BlockBreakListener implements Listener {

        private final Arena arena;

        public BlockBreakListener(Arena arena) {
            this.arena = arena;
        }

        @EventHandler
        public void onBlockBreak(@NotNull BlockBreakEvent event) {
            if (arena.getState() != GameState.RUNNING) {
                return; // ignore if game not running
            }

            //BedwarsPlugin.log("Block break event at " + event.getBlock().getLocation() + " by " + event.getPlayer().getName());
            Player player = event.getPlayer();
            Block broken = event.getBlock();

            handleBedBreak(player, broken, event);
        }

        private void handleBedBreak(Player player, @NotNull Block broken, @NotNull BlockBreakEvent event) {
            // Only care about beds
            if (!broken.getName().toLowerCase().contains("bed")) {
                return;
            }

            for (Team team : arena.getTeams()) {
                Vector3 bedPos = team.getBed(); // should be saved as exact block coords, not .5 offsets

                boolean isMainBed = broken.getFloorX() == bedPos.getFloorX() &&
                        broken.getFloorY() == bedPos.getFloorY() &&
                        broken.getFloorZ() == bedPos.getFloorZ();

                // if not main bed, check the adjacent half
                boolean isOtherHalf = false;

                //BedwarsPlugin.log("Checking against team " + team.getColor() + " bed at " + team.getBed());
                //BedwarsPlugin.log("Checking block broken " + broken + " at " + broken.getLocation());
                if (!isMainBed) {

                    // iterate neighbors
                    int[][] offsets = { {1,0,0}, {-1,0,0}, {0,0,1}, {0,0,-1} };
                    for (int[] off : offsets) {
                        if (broken.getFloorX() + off[0] == bedPos.getFloorX() &&
                                broken.getFloorY() + off[1] == bedPos.getFloorY() &&
                                broken.getFloorZ() + off[2] == bedPos.getFloorZ()) {
                            isOtherHalf = true;
                            break;
                        }
                    }
                }

                if (isMainBed || isOtherHalf) {
                    if (team.getPlayers().contains(player)) {
                        // can't break own bed
                        player.sendMessage("§cYou cannot break your own team's bed!");
                        event.setCancelled();
                        return;
                    }

                    if (!team.isBedAlive()) {
                        player.sendMessage("§cThis team's bed is already destroyed!");
                        event.setCancelled();
                        return;
                    }

                    team.onBedDestroyed();
                    player.sendMessage("§aYou destroyed " + team.getColor() + " Team's bed!");
                    arena.checkWinCondition();
                    break;
                }
            }
        }
}
