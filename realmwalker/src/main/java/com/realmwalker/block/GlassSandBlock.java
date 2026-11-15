package com.realmwalker.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Glass Sand: turns into plain glass at sunrise (during the day, on random ticks). */
public class GlassSandBlock extends Block {
    public GlassSandBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isDay() && random.nextInt(16) == 0 && level.canSeeSky(pos.above())) {
            level.setBlockAndUpdate(pos, Blocks.GLASS.defaultBlockState());
        }
    }
}
