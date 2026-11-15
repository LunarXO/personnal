package com.realmwalker.block;

import com.realmwalker.ModDimensions;
import com.realmwalker.block.entity.WaystonePortalBlockEntity;
import com.realmwalker.item.RealmwalkerTeleporter;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RenderShape;

public class WaystonePortalBlock extends BaseEntityBlock {
    public WaystonePortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WaystonePortalBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof WaystonePortalBlockEntity portal)) {
            return;
        }
        long lastUse = entity.getPersistentData().getLong("realmwalker:portal_cd");
        if (level.getGameTime() - lastUse < 40L) {
            return;
        }
        entity.getPersistentData().putLong("realmwalker:portal_cd", level.getGameTime());
        ResourceKey<Level> target = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(portal.getTarget()));
        ResourceKey<Level> returnDim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(portal.getReturnDim()));
        if (target.equals(level.dimension())) {
            target = returnDim;
        }
        RealmwalkerTeleporter.teleport(entity, target);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0D, 0.05D, 0.0D);
        }
        if (random.nextInt(60) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    ModSoundEvents.PORTAL_AMBIENT.get(), SoundSource.BLOCKS, 0.4F, 0.8F + random.nextFloat() * 0.4F, false);
        }
    }
}
