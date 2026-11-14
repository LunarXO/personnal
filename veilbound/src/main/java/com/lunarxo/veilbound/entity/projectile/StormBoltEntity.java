package com.lunarxo.veilbound.entity.projectile;

import java.util.HashSet;
import java.util.Set;

import com.lunarxo.veilbound.registry.ModItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class StormBoltEntity extends ThrowableItemProjectile {
    public StormBoltEntity(EntityType<? extends StormBoltEntity> type, Level level) {
        super(type, level);
    }

    public StormBoltEntity(EntityType<? extends StormBoltEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.AETHERITE_INGOT.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.strike(result.getEntity().position());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.strike(result.getLocation());
    }

    private void strike(Vec3 pos) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
        if (bolt != null) {
            bolt.moveTo(pos.x, pos.y, pos.z);
            Entity owner = this.getOwner();
            if (owner instanceof ServerPlayer player) {
                bolt.setCause(player);
            }
            server.addFreshEntity(bolt);
        }
        Set<LivingEntity> chained = new HashSet<>();
        for (LivingEntity entity : server.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos.x - 3.0D, pos.y - 3.0D, pos.z - 3.0D, pos.x + 3.0D, pos.y + 3.0D, pos.z + 3.0D),
                e -> e != this.getOwner() && e.isAlive())) {
            chained.add(entity);
        }
        for (LivingEntity entity : chained) {
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), 6.0F);
        }
    }
}
