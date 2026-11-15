package com.realmwalker.entity.stormspire;

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

public class TempestSovereignEntity extends BossBase {
    private int attackCooldown = 0;

    public TempestSovereignEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "entity.realmwalker.tempest_sovereign", BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BossBase.bossAttributes(1000.0D, 22.0D, 0.28D);
    }

    public static boolean checkSpawn(EntityType<? extends TempestSovereignEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension().equals(ModDimensions.STORMSPIRE) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected int getPhaseCount() {
        return 3;
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
            this.attackCooldown = getPhase() >= 3 ? 40 : 60;
            bossAttack(getPhase(), target);
        }
    }

    private void bossAttack(int phase, LivingEntity target) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if (phase == 1) {
                for (Player p : serverLevel.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(24.0D))) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if (bolt != null) {
                        bolt.moveTo(p.getX(), p.getY(), p.getZ());
                        serverLevel.addFreshEntity(bolt);
                    }
                }
                com.realmwalker.entity.stormspire.StormHawkEntity hawk = com.realmwalker.registry.ModEntities.STORM_HAWK.get().create(serverLevel);
                if (hawk != null) {
                    hawk.moveTo(this.getX(), this.getY() + 6, this.getZ());
                    serverLevel.addFreshEntity(hawk);
                }
            } else if (phase == 2) {
                if (target instanceof Player p) {
                    p.setDeltaMovement(p.getDeltaMovement().add(0.0D, 2.5D, 0.0D));
                    p.hasImpulse = true;
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    double angle = this.random.nextDouble() * Math.PI * 2;
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if (bolt != null) {
                        bolt.moveTo(this.getX() + Math.cos(angle) * 8, this.getY(), this.getZ() + Math.sin(angle) * 8);
                        serverLevel.addFreshEntity(bolt);
                    }
                }
                for (Player p : serverLevel.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(20.0D))) {
                    p.hurt(this.damageSources().lightningBolt(), 8.0F);
                }
            }
        }
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(ModItems.STORM_CROWN_SHARD.get()));
        }
    }
}
