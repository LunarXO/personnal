package com.realmwalker.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.realmwalker.registry.ModBlocks;

/** A small personal home structure in the Everbloom, with a sign plaque for the player's name. */
public class EverbloomHomeFeature extends Feature<NoneFeatureConfiguration> {
    public EverbloomHomeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState wall = ModBlocks.PETAL_BLOCK.get().defaultBlockState();
        BlockState roof = Blocks.OAK_PLANKS.defaultBlockState();
        // 5x5 floor
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(origin.offset(dx, -1, dz), ModBlocks.BLOOM_GRASS.get().defaultBlockState(), 2);
            }
        }
        // walls with a door gap on the south
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                if (!edge) {
                    continue;
                }
                for (int y = 0; y <= 2; y++) {
                    boolean door = dz == 2 && dx == 0 && y <= 1;
                    level.setBlock(origin.offset(dx, y, dz), door ? Blocks.AIR.defaultBlockState() : wall, 2);
                }
            }
        }
        // roof
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(origin.offset(dx, 3, dz), roof, 2);
            }
        }
        // interior: crafting table, chest, a sunpetal and a name plaque (standing sign)
        level.setBlock(origin.offset(-1, 0, -1), Blocks.CRAFTING_TABLE.defaultBlockState(), 2);
        level.setBlock(origin.offset(1, 0, -1), Blocks.CHEST.defaultBlockState(), 2);
        level.setBlock(origin.offset(0, 0, 1), ModBlocks.SUNPETAL.get().defaultBlockState(), 2);
        BlockState sign = Blocks.OAK_SIGN.defaultBlockState();
        level.setBlock(origin.offset(0, 0, 2).relative(Direction.NORTH), sign, 2);
        return true;
    }
}
