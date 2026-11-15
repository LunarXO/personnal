package com.realmwalker.entity.projectile;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

/**
 * Generic bolt used by ranged mobs and some weapons. Carries optional damage and a status effect.
 */
public class RealmwalkerProjectile extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(RealmwalkerProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_EFFECT = SynchedEntityData.defineId(RealmwalkerProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_EFFECT_DURATION = SynchedEntityData.defineId(RealmwalkerProjectile.class, EntityDataSerializers.INT);

    public RealmwalkerProjectile(EntityType<? extends RealmwalkerProjectile> type, Level level) {
        super(type, level);
    }

    public RealmwalkerProjectile(EntityType<? extends RealmwalkerProjectile> type, Level level, LivingEntity shooter) {
        super(type, shooter, level);
    }

    public void setBoltDamage(float damage) {
        this.entityData.set(DATA_DAMAGE, damage);
    }

    public float getBoltDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setEffect(Holder<MobEffect> effect, int durationTicks) {
        this.entityData.set(DATA_EFFECT, BuiltInRegistries.MOB_EFFECT.getKey(effect.value()).toString());
        this.entityData.set(DATA_EFFECT_DURATION, durationTicks);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DAMAGE, 4.0F);
        builder.define(DATA_EFFECT, "");
        builder.define(DATA_EFFECT_DURATION, 0);
    }

    @Override
    protected net.minecraft.world.item.Item getDefaultItem() {
        return net.minecraft.world.item.Items.FIRE_CHARGE;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide && hitResult.getEntity() instanceof LivingEntity living) {
            DamageSource source = this.damageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity owner ? owner : null);
            living.hurt(source, this.getBoltDamage());
            String effectId = this.entityData.get(DATA_EFFECT);
            int duration = this.entityData.get(DATA_EFFECT_DURATION);
            if (!effectId.isEmpty() && duration > 0) {
                var holder = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId));
                holder.ifPresent(effect -> living.addEffect(new MobEffectInstance(effect, duration)));
            }
        }
        this.discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag, HolderLookup.Provider registries) {
        super.addAdditionalSaveData(tag, registries);
        tag.putFloat("bolt_damage", this.getBoltDamage());
        tag.putString("effect", this.entityData.get(DATA_EFFECT));
        tag.putInt("effect_duration", this.entityData.get(DATA_EFFECT_DURATION));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag, HolderLookup.Provider registries) {
        super.readAdditionalSaveData(tag, registries);
        this.entityData.set(DATA_DAMAGE, tag.getFloat("bolt_damage"));
        this.entityData.set(DATA_EFFECT, tag.getString("effect"));
        this.entityData.set(DATA_EFFECT_DURATION, tag.getInt("effect_duration"));
    }
}
