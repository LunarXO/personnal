package com.lunarxo.veilbound.entity.projectile;

import com.lunarxo.veilbound.registry.ModItems;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EmberOrbEntity extends ThrowableItemProjectile {
    public EmberOrbEntity(EntityType<? extends EmberOrbEntity> type, Level level) {
        super(type, level);
    }

    public EmberOrbEntity(EntityType<? extends EmberOrbEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EMBER_CORE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        target.igniteForSeconds(4.0F);
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 6.0F);
    }
}
