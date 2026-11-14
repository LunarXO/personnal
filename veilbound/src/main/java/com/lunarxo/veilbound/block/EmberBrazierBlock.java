package com.lunarxo.veilbound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class EmberBrazierBlock extends Block {
    public EmberBrazierBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.5D;
            double y = pos.getY() + 0.9D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.5D;
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.02D, 0.0D);
            if (random.nextInt(6) == 0) {
                level.addParticle(ParticleTypes.SMOKE, x, y + 0.3D, z, 0.0D, 0.03D, 0.0D);
            }
        }
    }
}
