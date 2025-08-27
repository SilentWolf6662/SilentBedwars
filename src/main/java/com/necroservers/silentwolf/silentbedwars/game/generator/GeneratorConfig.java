package com.necroservers.silentwolf.silentbedwars.game.generator;

import cn.nukkit.item.Item;
import lombok.Getter;

@Getter
public class GeneratorConfig {
    private final Item item;
    private final int interval;
    private final int cap;

    public GeneratorConfig(Item item, int interval, int cap) {
        this.item = item;
        this.interval = interval;
        this.cap = cap;
    }

}
