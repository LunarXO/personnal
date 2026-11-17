package com.lunarxo.gadgetworks.entity;

import com.lunarxo.gadgetworks.registry.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class RedstoneSpark extends ThrowableItemProjectile {
    public RedstoneSpark(EntityType<? extends RedstoneSpark> type, Level level) {
        super(type, level);
    }

    public RedstoneSpark(EntityType<? extends RedstoneSpark> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.REDSTONE_SPARK.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount > 1) {
            level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                    getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        } else if (tickCount > 60) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = getOwner();
        target.hurt(damageSources().thrown(this, owner), 6.0F);
        if (target instanceof LivingEntity living) {
            living.invulnerableTime = 0;
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    16, 0.3D, 0.3D, 0.3D, 0.1D);
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                    net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 0.6F);
        }
    }

    @Override
    protected void onHit(net.minecraft.world.phys.HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SparkTicks", tickCount);
    }
}
