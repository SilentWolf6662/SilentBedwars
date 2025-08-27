package com.necroservers.silentwolf.silentbedwars.game.generator;

import java.util.ArrayList;
import java.util.List;

public class GeneratorManager {
    private final List<Generator> generators = new ArrayList<>();

    public void addGenerator(Generator g) {
        generators.add(g);
    }

    public void clear() {
        generators.clear();
    }

    public void tickAll(int currentTick) {
        for (Generator g : generators) {
            g.tick(currentTick);
        }
    }
}
