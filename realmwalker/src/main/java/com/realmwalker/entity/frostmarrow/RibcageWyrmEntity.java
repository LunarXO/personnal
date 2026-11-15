package com.realmwalker.entity.frostmarrow;

import com.realmwalker.ModDimensions;
import com.realmwalker.entity.BossBase;
import com.realmwalker.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import com.realmwalker.entity.projectile.RealmwalkerProjectile;
import net.minecraft.world.entity.AreaEffectCloud;

public class RibcageWyrmEntity extends BossBase {
    private int attackCooldown = 0;

    public RibcageWyrmEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "entity.realmwalker.ribcage_wyrm", BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BossBase.bossAttributes(800.0D, 20.0D, 0.3D);
    }

    public static boolean checkSpawn(EntityType<? extends RibcageWyrmEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension().equals(ModDimensions.FROSTMARROW) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected int getPhaseCount() {
        return 2;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.attackCooldown == 0) {
            this.attackCooldown = getPhase() >= 2 ? 40 : 60;
            bossAttack(getPhase(), target);
        }
    }

    private void bossAttack(int phase, LivingEntity target) {
        if (phase == 1) {
            if (target != null) {
                Vec3 charge = target.position().subtract(this.position()).normalize().scale(1.5D);
                this.setDeltaMovement(charge.x, 0.4D, charge.z);
                this.hasImpulse = true;
                for (int i = -1; i <= 1; i++) {
                    RealmwalkerProjectile breath = new RealmwalkerProjectile(com.realmwalker.registry.ModEntities.MOB_BOLT.get(), this.level(), this);
                    breath.setBoltDamage(9.0F);
                    breath.setEffect(MobEffects.MOVEMENT_SLOWDOWN, 120);
                    double dx = target.getX() - this.getX();
                    double dy = target.getY(0.5D) - this.getEyeY();
                    double dz = target.getZ() - this.getZ();
                    breath.shoot(dx + i * 2, dy, dz + i * 2, 2.0F, 0.5F);
                    this.level().addFreshEntity(breath);
                }
            }
        } else {
            for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(24.0D))) {
                p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
            }
            for (int i = 0; i < 2; i++) {
                com.realmwalker.entity.frostmarrow.RimeWolfEntity wolf = com.realmwalker.registry.ModEntities.RIME_WOLF.get().create(this.level());
                if (wolf != null) {
                    wolf.moveTo(this.getX() + this.random.nextDouble() * 8 - 4, this.getY(), this.getZ() + this.random.nextDouble() * 8 - 4);
                    this.level().addFreshEntity(wolf);
                }
            }
        }
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(ModItems.FROZEN_HEARTBONE.get()));
        }
    }
}
