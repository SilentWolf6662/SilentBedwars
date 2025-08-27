package com.necroservers.silentwolf.silentbedwars.game.generator;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TeamGenerator extends Generator {
    private final Map<Item, GeneratorConfig> items = new HashMap<>(); // Item -> spawn interval

    public TeamGenerator(String name, Level level, Vector3 location) {
        super(name, level, location, 0); // interval unused here
    }

    public void addItem(Item item, int interval, int cap) {
        items.put(item, new GeneratorConfig(item, interval, cap));
    }

    @Override
    public void tick(int currentTick) {
        for (GeneratorConfig config : items.values()) {
            if (currentTick % config.getInterval() != 0) continue;

            int nearbyCount = 0;
            for (Entity entity : level.getEntities()) {
                if (entity instanceof EntityItem &&
                        entity.getPosition().distance(location) < 2) {
                    Item stack = ((EntityItem) entity).getItem();
                    if (Objects.equals(stack.getId(), config.getItem().getId())) {
                        nearbyCount += stack.getCount();
                    }
                }
            }

            if (nearbyCount < config.getCap()) {
                level.dropItem(location, config.getItem().clone());
            }
        }
    }
}
