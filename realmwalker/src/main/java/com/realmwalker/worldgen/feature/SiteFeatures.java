package com.realmwalker.worldgen.feature;

import com.realmwalker.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SiteFeatures {

    private SiteFeatures() {
    }

    /** Overworld shrine holding a Waystone Shard (and the first Codex). */
public static class WaystoneShrineFeature extends Feature<NoneFeatureConfiguration> {
    public WaystoneShrineFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState bricks = Blocks.STONE_BRICKS.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(origin.offset(dx, -1, dz), bricks, 2);
            }
        }
        level.setBlock(origin, Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 2);
        level.setBlock(origin.above(), bricks, 2);
        level.setBlock(origin.above(2), ModBlocks.WAYSTONE_SHRINE.get().defaultBlockState(), 2);
        level.setBlock(origin.above(3), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);
        return true;
    }
}

/** A Glowcap Shrine in Lumenfen. */
public static class GlowcapShrineFeature extends Feature<NoneFeatureConfiguration> {
    public GlowcapShrineFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        level.setBlock(origin.below(), ModBlocks.MIREWOOD_PLANKS.get().defaultBlockState(), 2);
        level.setBlock(origin, ModBlocks.GLOWCAP_SHRINE.get().defaultBlockState(), 2);
        level.setBlock(origin.above(), ModBlocks.GLOWCAP.get().defaultBlockState(), 2);
        return true;
    }
}

/** A buried ruin in the Saltglass Expanse, marked by a sand pillar. */
public static class BuriedRuinFeature extends Feature<NoneFeatureConfiguration> {
    public BuriedRuinFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        level.setBlock(origin, ModBlocks.BURIED_RUIN.get().defaultBlockState(), 2);
        level.setBlock(origin.below(), ModBlocks.SUNKEN_BRICK.get().defaultBlockState(), 2);
        for (int y = 1; y <= 4; y++) {
            level.setBlock(origin.above(y), Blocks.SAND.defaultBlockState(), 2);
        }
        return true;
    }
}

/** A broken Gear Engine site in Cogspire. */
public static class GearEngineFeature extends Feature<NoneFeatureConfiguration> {
    public GearEngineFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(origin.offset(dx, -1, dz), ModBlocks.COG_PLATING.get().defaultBlockState(), 2);
            }
        }
        level.setBlock(origin, ModBlocks.GEAR_ENGINE.get().defaultBlockState(), 2);
        level.setBlock(origin.above(), ModBlocks.FURNACE_GLASS.get().defaultBlockState(), 2);
        return true;
    }
}

/** An unlit beacon fire site in Frostmarrow. */
public static class BeaconFireSiteFeature extends Feature<NoneFeatureConfiguration> {
    public BeaconFireSiteFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(origin.offset(dx, -1, dz), Blocks.SNOW_BLOCK.defaultBlockState(), 2);
            }
        }
        level.setBlock(origin, ModBlocks.BEACON_FIRE_SITE.get().defaultBlockState(), 2);
        return true;
    }
}

/** A marked spire in Stormspire where a Lightning Rod can be placed. */
public static class MarkedSpireFeature extends Feature<NoneFeatureConfiguration> {
    public MarkedSpireFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        for (int y = 0; y <= 2; y++) {
            level.setBlock(origin.below(y), ModBlocks.CLOUDSTONE.get().defaultBlockState(), 2);
        }
        level.setBlock(origin, ModBlocks.MARKED_SPIRE.get().defaultBlockState(), 2);
        level.setBlock(origin.above(), ModBlocks.STORMGLASS.get().defaultBlockState(), 2);
        return true;
    }
}
}
