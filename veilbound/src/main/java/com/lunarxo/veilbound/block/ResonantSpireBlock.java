package com.lunarxo.veilbound.block;

import com.lunarxo.veilbound.block.entity.ResonantSpireBlockEntity;
import com.lunarxo.veilbound.registry.ModBlockEntities;
import com.lunarxo.veilbound.registry.ModParticles;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ResonantSpireBlock extends BaseEntityBlock {
    public ResonantSpireBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ResonantSpireBlock> codec() {
        return simpleCodec(ResonantSpireBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonantSpireBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.RESONANT_SPIRE.get(), ResonantSpireBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) == 0) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.6D;
            double y = pos.getY() + 1.0D + random.nextDouble() * 0.5D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.6D;
            level.addParticle((ParticleOptions) ModParticles.SHARD_SPARKLE.get(), x, y, z, 0.0D, 0.04D, 0.0D);
        }
    }
}
