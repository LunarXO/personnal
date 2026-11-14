package com.lunarxo.veilbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ModDimensions {
    public static final ResourceKey<Level> THE_VEIL = ResourceKey.create(Registries.DIMENSION, Veilbound.id("the_veil"));

    private ModDimensions() {
    }
}
