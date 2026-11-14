package com.lunarxo.veilbound.entity.ai;

import java.util.EnumSet;

import com.lunarxo.veilbound.entity.HollowKingEntity;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class VoidSlamGoal extends Goal {
    private final HollowKingEntity boss;
    private int cooldown;

    public VoidSlamGoal(HollowKingEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.boss.getTarget();
        return target != null && target.isAlive() && this.boss.distanceToSqr(target) < 36.0D && this.cooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        this.cooldown = 90;
        if (this.boss.level() instanceof ServerLevel server) {
            for (LivingEntity entity : server.getEntitiesOfClass(LivingEntity.class, this.boss.getBoundingBox().inflate(6.0D),
                    e -> e != this.boss && e.isAlive())) {
                entity.hurt(this.boss.damageSources().mobAttack(this.boss), 12.0F);
                entity.knockback(1.2D, entity.getX() - this.boss.getX(), entity.getZ() - this.boss.getZ());
                entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 0));
            }
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.boss.getX(), this.boss.getY() + 1.0D, this.boss.getZ(), 3, 2.0D, 1.0D, 2.0D, 0.0D);
            server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.boss.getX(), this.boss.getY() + 1.0D, this.boss.getZ(), 60, 3.0D, 1.0D, 3.0D, 0.05D);
            this.boss.playSound(ModSoundEvents.BOSS_ROAR.get(), 2.0F, 0.5F);
        }
    }

    @Override
    public void tick() {
        this.cooldown--;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
