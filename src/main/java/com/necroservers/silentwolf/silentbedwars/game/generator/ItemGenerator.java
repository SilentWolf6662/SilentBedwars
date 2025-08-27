package com.necroservers.silentwolf.silentbedwars.game.generator;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.Objects;

public class ItemGenerator extends Generator {
    private final Item item;
    private final int cap;

    public ItemGenerator(String name, Level level, Vector3 location, int interval, String item, int cap) {
        super(name, level, location, interval);
        this.item = Item.get(item);
        this.cap = cap;
    }

    @Override
    public void tick(int currentTick) {
        if (currentTick % interval != 0) return;

        int nearbyCount = 0;
        for (Entity entity : level.getEntities()) {
            if (entity instanceof EntityItem &&
                    entity.getPosition().distance(location) < 2) {
                Item stack = ((EntityItem) entity).getItem();
                if (Objects.equals(stack.getId(), item.getId())) {
                    nearbyCount += stack.getCount();
                }
            }
        }

        if (nearbyCount < cap) {
            level.dropItem(location, item.clone());
        }
    }
}
