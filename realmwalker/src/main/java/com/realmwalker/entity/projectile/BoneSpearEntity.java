package com.realmwalker.entity.projectile;

import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModItems;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/** Throwable Bone Spear projectile. */
public class BoneSpearEntity extends ThrowableItemProjectile {
    public BoneSpearEntity(EntityType<? extends BoneSpearEntity> type, Level level) {
        super(type, level);
    }

    public BoneSpearEntity(Level level, LivingEntity owner) {
        super(ModEntities.BONE_SPEAR.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BONE_SPEAR.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity living) {
            DamageSource source = this.damageSources().thrown(this, this.getOwner() instanceof LivingEntity owner ? owner : null);
            living.hurt(source, 7.0F);
        }
    }
}
