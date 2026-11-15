package com.realmwalker.worldgen.feature;

import com.realmwalker.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A circular boss arena: a walled platform with an Arena Heart at its center.
 * The floor and wall blocks are chosen per dimension.
 */
public class ArenaFeature extends Feature<NoneFeatureConfiguration> {
    private static final int RADIUS = 14;
    private static final int WALL_HEIGHT = 6;

    private final Block floor;
    private final Block wall;

    public ArenaFeature(Block floor, Block wall) {
        super(NoneFeatureConfiguration.CODEC);
        this.floor = floor;
        this.wall = wall;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos center = context.origin();
        BlockState floorState = this.floor.defaultBlockState();
        BlockState wallState = this.wall.defaultBlockState();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > RADIUS) {
                    continue;
                }
                BlockPos p = center.offset(dx, 0, dz);
                level.setBlock(p.below(), floorState, 2);
                for (int y = 0; y < 3; y++) {
                    level.setBlock(p.below(y + 1), Blocks.AIR.defaultBlockState(), 2);
                }
                if (dist > RADIUS - 1.5) {
                    for (int y = 1; y <= WALL_HEIGHT; y++) {
                        // leave an entrance gap on the south side
                        if (dz == RADIUS && Math.abs(dx) <= 1 && y <= 3) {
                            continue;
                        }
                        level.setBlock(center.offset(dx, y, dz), wallState, 2);
                    }
                }
            }
        }
        level.setBlock(center, ModBlocks.ARENA_HEART.get().defaultBlockState(), 2);
        level.setBlock(center.above(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(center.above(2), Blocks.AIR.defaultBlockState(), 2);
        return true;
    }
}
