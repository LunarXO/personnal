package com.realmwalker.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Murk Water: a decorative, non-colliding, translucent water-like block for Lumenfen. */
public class MurkWaterBlock extends Block {
    public MurkWaterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.BUBBLE, pos.getX() + random.nextDouble(), pos.getY() + 0.8D, pos.getZ() + random.nextDouble(), 0.0D, 0.05D, 0.0D);
        }
    }
}
