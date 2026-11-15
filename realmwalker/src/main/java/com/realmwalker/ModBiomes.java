package com.realmwalker;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> GLOWMIRE = ResourceKey.create(Registries.BIOME, Realmwalker.id("glowmire"));
    public static final ResourceKey<Biome> SPOREWOOD = ResourceKey.create(Registries.BIOME, Realmwalker.id("sporewood"));
    public static final ResourceKey<Biome> HOLLOW_MERE = ResourceKey.create(Registries.BIOME, Realmwalker.id("hollow_mere"));

    public static final ResourceKey<Biome> SALT_FLATS = ResourceKey.create(Registries.BIOME, Realmwalker.id("salt_flats"));
    public static final ResourceKey<Biome> GLASS_DUNES = ResourceKey.create(Registries.BIOME, Realmwalker.id("glass_dunes"));
    public static final ResourceKey<Biome> DROWNED_BASIN = ResourceKey.create(Registries.BIOME, Realmwalker.id("drowned_basin"));

    public static final ResourceKey<Biome> GEARFIELDS = ResourceKey.create(Registries.BIOME, Realmwalker.id("gearfields"));
    public static final ResourceKey<Biome> STEAM_CANYON = ResourceKey.create(Registries.BIOME, Realmwalker.id("steam_canyon"));
    public static final ResourceKey<Biome> FORGE_DEPTHS = ResourceKey.create(Registries.BIOME, Realmwalker.id("forge_depths"));

    public static final ResourceKey<Biome> WHITEOUT_PLAINS = ResourceKey.create(Registries.BIOME, Realmwalker.id("whiteout_plains"));
    public static final ResourceKey<Biome> RIB_VALLEY = ResourceKey.create(Registries.BIOME, Realmwalker.id("rib_valley"));
    public static final ResourceKey<Biome> AURORA_PEAKS = ResourceKey.create(Registries.BIOME, Realmwalker.id("aurora_peaks"));

    public static final ResourceKey<Biome> CALM_ISLES = ResourceKey.create(Registries.BIOME, Realmwalker.id("calm_isles"));
    public static final ResourceKey<Biome> THUNDER_REACH = ResourceKey.create(Registries.BIOME, Realmwalker.id("thunder_reach"));
    public static final ResourceKey<Biome> SPIRE_SUMMIT = ResourceKey.create(Registries.BIOME, Realmwalker.id("spire_summit"));

    public static final ResourceKey<Biome> BLOOM_MEADOW = ResourceKey.create(Registries.BIOME, Realmwalker.id("bloom_meadow"));
    public static final ResourceKey<Biome> GLASS_GARDEN = ResourceKey.create(Registries.BIOME, Realmwalker.id("glass_garden"));
    public static final ResourceKey<Biome> AURORA_GLADE = ResourceKey.create(Registries.BIOME, Realmwalker.id("aurora_glade"));

    public static final TagKey<Biome> IS_FROSTMARROW = TagKey.create(Registries.BIOME, Realmwalker.id("is_frostmarrow"));

    private ModBiomes() {
    }
}
