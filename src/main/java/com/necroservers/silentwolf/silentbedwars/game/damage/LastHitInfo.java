package com.necroservers.silentwolf.silentbedwars.game.damage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LastHitInfo {
    private UUID damager;
    private DamageCauseType cause;
    private long timestamp;

    public LastHitInfo(UUID damager, DamageCauseType cause, long timestamp) {
        this.damager = damager;
        this.cause = cause;
        this.timestamp = timestamp;
    }
}
