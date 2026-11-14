package com.lunarxo.veilbound.entity.projectile;

import com.lunarxo.veilbound.registry.ModEffects;
import com.lunarxo.veilbound.registry.ModItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class GloomOrbEntity extends ThrowableItemProjectile {
    public GloomOrbEntity(EntityType<? extends GloomOrbEntity> type, Level level) {
        super(type, level);
    }

    public GloomOrbEntity(EntityType<? extends GloomOrbEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.VEIL_ESSENCE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 6.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D),
                    e -> e != this.getOwner() && e.isAlive())) {
                nearby.hurt(this.damageSources().thrown(this, this.getOwner()), 4.0F);
                nearby.addEffect(new MobEffectInstance(ModEffects.EMBER_MARK, 100, 0));
                nearby.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
            }
        }
    }
}
