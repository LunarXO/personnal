package com.realmwalker.entity.saltglass_expanse;

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

public class PrismColossusEntity extends BossBase {
    private int attackCooldown = 0;

    public PrismColossusEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "entity.realmwalker.prism_colossus", BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BossBase.bossAttributes(550.0D, 16.0D, 0.24D);
    }

    public static boolean checkSpawn(EntityType<? extends PrismColossusEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension().equals(ModDimensions.SALTGLASS_EXPANSE) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
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
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4.0D;
                RealmwalkerProjectile bolt = new RealmwalkerProjectile(com.realmwalker.registry.ModEntities.MOB_BOLT.get(), this.level(), this);
                bolt.setBoltDamage(8.0F);
                bolt.setPos(this.getX(), this.getEyeY(), this.getZ());
                bolt.shoot(Math.cos(angle), 0.1D, Math.sin(angle), 1.8F, 0.5F);
                this.level().addFreshEntity(bolt);
            }
            for (int i = 0; i < 5; i++) {
                RealmwalkerProjectile shard = new RealmwalkerProjectile(com.realmwalker.registry.ModEntities.MOB_BOLT.get(), this.level(), this);
                shard.setBoltDamage(6.0F);
                shard.setPos(this.getX() + this.random.nextDouble() * 8 - 4, this.getY() + 6, this.getZ() + this.random.nextDouble() * 8 - 4);
                shard.shoot(0.0D, -1.0D, 0.0D, 1.2F, 2.0F);
                this.level().addFreshEntity(shard);
            }
        } else {
            if (target != null) {
                for (int i = -1; i <= 1; i++) {
                    RealmwalkerProjectile beam = new RealmwalkerProjectile(com.realmwalker.registry.ModEntities.MOB_BOLT.get(), this.level(), this);
                    beam.setBoltDamage(10.0F);
                    double dx = target.getX() - this.getX();
                    double dy = target.getY(0.5D) - this.getEyeY();
                    double dz = target.getZ() - this.getZ();
                    beam.shoot(dx + i * 2, dy, dz + i * 2, 2.0F, 0.2F);
                    this.level().addFreshEntity(beam);
                }
            }
            for (int i = 0; i < 2; i++) {
                com.realmwalker.entity.saltglass_expanse.GlassScorpionEntity scorpion = com.realmwalker.registry.ModEntities.GLASS_SCORPION.get().create(this.level());
                if (scorpion != null) {
                    scorpion.moveTo(this.getX() + this.random.nextDouble() * 6 - 3, this.getY(), this.getZ() + this.random.nextDouble() * 6 - 3);
                    this.level().addFreshEntity(scorpion);
                }
            }
        }
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(ModItems.PRISM_CORE.get()));
        }
    }
}
