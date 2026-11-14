package com.lunarxo.veilbound.entity.ai;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.lunarxo.veilbound.entity.HollowKingEntity;
import com.lunarxo.veilbound.registry.ModEffects;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VoidBeamGoal extends Goal {
    private static final int CHARGE_TICKS = 40;
    private static final int BEAM_RANGE = 24;

    private final HollowKingEntity boss;
    private int cooldown;
    private int state; // 0 = idle, 1 = charging, 2 = fired
    private int timer;

    public VoidBeamGoal(HollowKingEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.boss.getTarget();
        return this.boss.getPhase() == 3 && target != null && target.isAlive()
                && this.boss.distanceToSqr(target) < 576.0D && this.cooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.state == 1;
    }

    @Override
    public void start() {
        this.state = 1;
        this.timer = CHARGE_TICKS;
        this.boss.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.cooldown--;
        LivingEntity target = this.boss.getTarget();
        if (this.state == 1 && target != null) {
            this.timer--;
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.boss.level() instanceof ServerLevel server) {
                Vec3 eye = target.getEyePosition();
                server.sendParticles(ParticleTypes.END_ROD, eye.x, eye.y, eye.z, 2, 0.3D, 0.3D, 0.3D, 0.0D);
            }
            if (this.timer <= 0) {
                this.fireBeam(target);
                this.state = 2;
                this.cooldown = 120;
            }
        }
    }

    private void fireBeam(LivingEntity target) {
        if (!(this.boss.level() instanceof ServerLevel server)) {
            return;
        }
        Vec3 eye = this.boss.getEyePosition();
        Vec3 look = this.boss.getLookAngle();
        Set<LivingEntity> hit = new HashSet<>();
        for (int step = 2; step <= BEAM_RANGE; step++) {
            Vec3 point = eye.add(look.scale(step));
            for (LivingEntity entity : server.getEntitiesOfClass(LivingEntity.class, new AABB(point, point).inflate(1.2D),
                    e -> e != this.boss && e.isAlive())) {
                hit.add(entity);
            }
            server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 3, 0.15D, 0.15D, 0.15D, 0.01D);
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 2, 0.2D, 0.2D, 0.2D, 0.02D);
        }
        for (LivingEntity entity : hit) {
            entity.hurt(this.boss.damageSources().mobAttack(this.boss), 14.0F);
            entity.addEffect(new MobEffectInstance(ModEffects.EMBER_MARK, 100, 0));
        }
        server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, eye.x + look.x * 4.0D, eye.y + look.y * 4.0D, eye.z + look.z * 4.0D, 1, 0.5D, 0.5D, 0.5D, 0.0D);
        this.boss.playSound(ModSoundEvents.BOSS_ROAR.get(), 2.0F, 0.4F);
    }

    @Override
    public void stop() {
        this.state = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
