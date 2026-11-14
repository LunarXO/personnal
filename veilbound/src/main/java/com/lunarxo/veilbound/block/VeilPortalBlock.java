package com.lunarxo.veilbound.block;

import com.lunarxo.veilbound.VeilboundConfig;
import com.lunarxo.veilbound.item.VeilTeleporter;
import com.lunarxo.veilbound.registry.ModParticles;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class VeilPortalBlock extends Block {
    public VeilPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        long lastUse = entity.getPersistentData().getLong("veilbound:portal_cd");
        if (level.getGameTime() - lastUse < VeilboundConfig.portalCooldownTicks) {
            return;
        }
        entity.getPersistentData().putLong("veilbound:portal_cd", level.getGameTime());
        VeilTeleporter.teleport(entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            level.addParticle((ParticleOptions) ModParticles.SHARD_SPARKLE.get(), x, y, z, 0.0D, 0.05D, 0.0D);
        }
        if (random.nextInt(40) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    ModSoundEvents.PORTAL_AMBIENT.get(), SoundSource.BLOCKS, 0.4F, 0.8F + random.nextFloat() * 0.4F, false);
        }
    }
}
