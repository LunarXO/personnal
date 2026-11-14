package com.lunarxo.veilbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> VEIL_EXPANSE = ResourceKey.create(Registries.BIOME, Veilbound.id("veil_expanse"));
    public static final ResourceKey<Biome> CRYSTAL_HOLLOWS = ResourceKey.create(Registries.BIOME, Veilbound.id("crystal_hollows"));
    public static final ResourceKey<Biome> EMBER_MIRES = ResourceKey.create(Registries.BIOME, Veilbound.id("ember_mires"));

    public static final TagKey<Biome> IS_VEIL = TagKey.create(Registries.BIOME, Veilbound.id("is_veil"));

    private ModBiomes() {
    }
}
