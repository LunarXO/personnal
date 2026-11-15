package com.realmwalker.entity.projectile;

import com.realmwalker.registry.ModEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Grapple hook projectile: pulls the shooter toward the hit entity/point. */
public class HookEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_OWNER = SynchedEntityData.defineId(HookEntity.class, EntityDataSerializers.INT);

    public HookEntity(EntityType<? extends HookEntity> type, Level level) {
        super(type, level);
    }

    public HookEntity(Level level, LivingEntity owner) {
        super(ModEntities.HOOK.get(), owner, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, -1);
    }

    @Override
    protected net.minecraft.world.item.Item getDefaultItem() {
        return com.realmwalker.registry.ModItems.LURKERS_HOOK.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (!this.level().isClientSide && owner instanceof Player player && target instanceof LivingEntity living) {
            Vec3 pull = player.position().subtract(living.position()).normalize().scale(1.6D);
            living.setDeltaMovement(living.getDeltaMovement().add(pull));
            living.hasImpulse = true;
        }
        this.discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner instanceof Player player) {
                Vec3 pull = this.position().subtract(player.position()).normalize().scale(1.2D);
                player.setDeltaMovement(player.getDeltaMovement().add(pull));
                player.hasImpulse = true;
                player.fallDistance = 0.0F;
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getOwner() == null) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.addAdditionalSaveData(tag, registries);
        tag.putInt("owner", this.entityData.get(DATA_OWNER));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.readAdditionalSaveData(tag, registries);
        this.entityData.set(DATA_OWNER, tag.getInt("owner"));
    }
}
