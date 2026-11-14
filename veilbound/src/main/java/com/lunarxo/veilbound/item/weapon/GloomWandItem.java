package com.lunarxo.veilbound.item.weapon;

import com.lunarxo.veilbound.entity.projectile.GloomOrbEntity;
import com.lunarxo.veilbound.registry.ModEntities;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class GloomWandItem extends ChargeWeaponItem {
    public GloomWandItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter) {
        return new GloomOrbEntity(ModEntities.GLOOM_ORB.get(), shooter, level);
    }

    @Override
    protected SoundEvent getShootSound() {
        return ModSoundEvents.GLOOM_CAST.get();
    }
}
