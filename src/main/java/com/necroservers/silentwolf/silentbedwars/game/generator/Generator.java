package com.necroservers.silentwolf.silentbedwars.game.generator;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

public abstract class Generator {
    protected final String name;
    protected final Vector3 location;
    protected final Level level;
    protected int interval; // ticks between spawns

    public Generator(String name, Level level, Vector3 location, int interval) {
        this.name = name;
        this.level = level;
        this.location = location;
        this.interval = interval;
    }

    public abstract void tick(int currentTick); // called every tick
}