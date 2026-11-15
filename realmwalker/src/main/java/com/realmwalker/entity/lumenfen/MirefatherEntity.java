package com.realmwalker.entity.lumenfen;

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

public class MirefatherEntity extends BossBase {
    private int attackCooldown = 0;

    public MirefatherEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "entity.realmwalker.mirefather", BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BossBase.bossAttributes(400.0D, 14.0D, 0.26D);
    }

    public static boolean checkSpawn(EntityType<? extends MirefatherEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension().equals(ModDimensions.LUMENFEN) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
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
            for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0D))) {
                p.hurt(this.damageSources().mobAttack(this), 10.0F);
            }
            for (int i = 0; i < 2; i++) {
                com.realmwalker.entity.lumenfen.CaplingEntity capling = com.realmwalker.registry.ModEntities.CAPLING.get().create(this.level());
                if (capling != null) {
                    capling.moveTo(this.getX() + this.random.nextDouble() * 6 - 3, this.getY(), this.getZ() + this.random.nextDouble() * 6 - 3);
                    this.level().addFreshEntity(capling);
                }
            }
        } else {
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            cloud.setRadius(6.0F);
            cloud.setDuration(160);
            cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 1));
            this.level().addFreshEntity(cloud);
            if (target != null) {
                for (int i = 0; i < 3; i++) {
                    Vec3 dir = target.position().subtract(this.position()).normalize();
                    AreaEffectCloud line = new AreaEffectCloud(this.level(),
                            this.getX() + dir.x * (4 + i * 3), this.getY(), this.getZ() + dir.z * (4 + i * 3));
                    line.setRadius(1.5F);
                    line.setDuration(120);
                    line.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
                    this.level().addFreshEntity(line);
                }
                for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0D))) {
                    p.hurt(this.damageSources().mobAttack(this), 6.0F);
                }
            }
        }
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(ModItems.HEART_OF_THE_MIRE.get()));
        }
    }
}
