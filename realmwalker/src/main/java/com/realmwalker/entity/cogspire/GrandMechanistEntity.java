package com.realmwalker.entity.cogspire;

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

public class GrandMechanistEntity extends BossBase {
    private int attackCooldown = 0;

    public GrandMechanistEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "entity.realmwalker.grand_mechanist", BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BossBase.bossAttributes(700.0D, 18.0D, 0.25D);
    }

    public static boolean checkSpawn(EntityType<? extends GrandMechanistEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension().equals(ModDimensions.COGSPIRE) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
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
        if (phase == 1) {
            for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D))) {
                p.hurt(this.damageSources().mobAttack(this), 12.0F);
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0D, this.getZ(), 30, 3.0D, 1.0D, 3.0D, 0.2D);
                for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(6.0D))) {
                    p.hurt(this.damageSources().hotFloor(), 4.0F);
                }
            }
        } else if (phase == 2) {
            for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0D))) {
                Vec3 push = p.position().subtract(this.position()).normalize().scale(2.0D);
                p.setDeltaMovement(p.getDeltaMovement().add(push.x, 0.2D, push.z));
                p.hasImpulse = true;
            }
            for (int i = 0; i < 2; i++) {
                com.realmwalker.entity.cogspire.RivetDroneEntity drone = com.realmwalker.registry.ModEntities.RIVET_DRONE.get().create(this.level());
                if (drone != null) {
                    drone.moveTo(this.getX() + this.random.nextDouble() * 6 - 3, this.getY() + 3, this.getZ() + this.random.nextDouble() * 6 - 3);
                    this.level().addFreshEntity(drone);
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                BlockPos p = this.blockPosition().offset(this.random.nextInt(21) - 10, 0, this.random.nextInt(21) - 10);
                if (this.level().getBlockState(p).isAir()) {
                    this.level().setBlockAndUpdate(p, net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState());
                }
            }
            for (Player p : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0D))) {
                p.hurt(this.damageSources().mobAttack(this), 8.0F);
            }
        }
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(ModItems.MASTER_GEAR.get()));
        }
    }
}
