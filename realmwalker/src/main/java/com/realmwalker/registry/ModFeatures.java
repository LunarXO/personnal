package com.realmwalker.registry;

import com.realmwalker.Realmwalker;
import com.realmwalker.worldgen.feature.ArenaFeature;
import com.realmwalker.worldgen.feature.EverbloomHomeFeature;
import com.realmwalker.worldgen.feature.SiteFeatures;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Realmwalker.MODID);

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> WAYSTONE_SHRINE =
            FEATURES.register("waystone_shrine", () -> new SiteFeatures.WaystoneShrineFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> GLOWCAP_SHRINE_SITE =
            FEATURES.register("glowcap_shrine_site", () -> new SiteFeatures.GlowcapShrineFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> BURIED_RUIN_SITE =
            FEATURES.register("buried_ruin_site", () -> new SiteFeatures.BuriedRuinFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> GEAR_ENGINE_SITE =
            FEATURES.register("gear_engine_site", () -> new SiteFeatures.GearEngineFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> BEACON_FIRE_SITE =
            FEATURES.register("beacon_fire_site", () -> new SiteFeatures.BeaconFireSiteFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> MARKED_SPIRE_SITE =
            FEATURES.register("marked_spire_site", () -> new SiteFeatures.MarkedSpireFeature());
    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> EVERBLOOM_HOME =
            FEATURES.register("everbloom_home", EverbloomHomeFeature::new);

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> ARENA_LUMENFEN =
            FEATURES.register("arena_lumenfen", () -> new ArenaFeature(ModBlocks.MIREWOOD_PLANKS.get(), ModBlocks.MIREWOOD_LOG.get()));

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> ARENA_SALTGLASS_EXPANSE =
            FEATURES.register("arena_saltglass_expanse", () -> new ArenaFeature(ModBlocks.SALT_BLOCK.get(), ModBlocks.SUNKEN_BRICK.get()));

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> ARENA_COGSPIRE =
            FEATURES.register("arena_cogspire", () -> new ArenaFeature(ModBlocks.COG_PLATING.get(), ModBlocks.BRASS_BLOCK.get()));

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> ARENA_FROSTMARROW =
            FEATURES.register("arena_frostmarrow", () -> new ArenaFeature(ModBlocks.PACKED_FROST.get(), ModBlocks.BONEICE.get()));

    public static final DeferredHolder<Feature<?>, ? extends Feature<NoneFeatureConfiguration>> ARENA_STORMSPIRE =
            FEATURES.register("arena_stormspire", () -> new ArenaFeature(ModBlocks.CLOUDSTONE.get(), ModBlocks.CHAIN_BLOCK.get()));


    private ModFeatures() {
    }
}
