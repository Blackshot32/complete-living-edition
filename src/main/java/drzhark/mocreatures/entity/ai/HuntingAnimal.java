/*
 * Adapted from Naturalist (Starfish Studios) under MIT License:
 * Copyright (c) Starfish Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package drzhark.mocreatures.entity.ai;

import net.minecraft.nbt.CompoundTag;

/**
 * Interface representing a predator animal capable of hunting with realistic cooldowns.
 * Prevents relentless massacres by imposing a rest/digestion period after successful hunts,
 * integrating seamlessly with Naturalist's ecological food chain mechanics.
 */
public interface HuntingAnimal {
    int HUNTING_COOLDOWN_TICKS = 2400; // 2 minutes (120 seconds) cooldown between hunts
    String HUNTING_COOLDOWN_TAG = "HuntingCooldown";

    int getHuntingCooldown();

    void setHuntingCooldown(int cooldown);

    default boolean canHunt() {
        return getHuntingCooldown() <= 0;
    }

    default void startHuntingCooldown() {
        setHuntingCooldown(HUNTING_COOLDOWN_TICKS);
    }

    default void tickHuntingCooldown() {
        if (getHuntingCooldown() > 0) {
            setHuntingCooldown(getHuntingCooldown() - 1);
        }
    }

    default void saveHuntingCooldown(CompoundTag tag) {
        tag.putInt(HUNTING_COOLDOWN_TAG, getHuntingCooldown());
    }

    default void loadHuntingCooldown(CompoundTag tag) {
        if (tag.contains(HUNTING_COOLDOWN_TAG)) {
            setHuntingCooldown(tag.getInt(HUNTING_COOLDOWN_TAG));
        }
    }
}
