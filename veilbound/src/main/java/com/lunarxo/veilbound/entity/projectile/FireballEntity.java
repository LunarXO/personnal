package com.lunarxo.veilbound.entity.projectile;

import com.lunarxo.veilbound.registry.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FireballEntity extends ThrowableItemProjectile {
    public FireballEntity(EntityType<? extends FireballEntity> type, Level level) {
        super(type, level);
    }

    public FireballEntity(EntityType<? extends FireballEntity> type, LivingEntity shooter, Level level) {
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
        target.igniteForSeconds(6.0F);
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 8.0F);
        this.explode(target.position());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.explode(result.getLocation());
    }

    private void explode(Vec3 pos) {
        if (this.level() instanceof ServerLevel server) {
            server.explode(this, pos.x, pos.y, pos.z, 2.0F, Level.ExplosionInteraction.NONE);
            server.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 30, 1.5D, 1.0D, 1.5D, 0.02D);
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 2, 1.0D, 0.5D, 1.0D, 0.0D);
        }
    }
}
