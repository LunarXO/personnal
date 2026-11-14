package com.lunarxo.veilbound.entity.ai;

import com.lunarxo.veilbound.entity.HollowKingEntity;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class BossTeleportGoal extends Goal {
    private final HollowKingEntity boss;
    private int cooldown;

    public BossTeleportGoal(HollowKingEntity boss) {
        this.boss = boss;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.boss.getTarget();
        return this.boss.getPhase() >= 2 && target != null && target.isAlive()
                && this.boss.distanceToSqr(target) > 100.0D && this.cooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        this.cooldown = 160;
        LivingEntity target = this.boss.getTarget();
        if (target == null || !(this.boss.level() instanceof ServerLevel server)) {
            return;
        }
        Vec3 behind = target.position().subtract(target.getLookAngle().scale(4.0D));
        double x = behind.x;
        double y = target.getY();
        double z = behind.z;
        server.sendParticles(ParticleTypes.PORTAL, this.boss.getX(), this.boss.getY() + 1.0D, this.boss.getZ(), 40, 0.5D, 1.0D, 0.5D, 0.1D);
        this.boss.teleportTo(x, y, z);
        server.sendParticles(ParticleTypes.PORTAL, x, y + 1.0D, z, 40, 0.5D, 1.0D, 0.5D, 0.1D);
        this.boss.playSound(ModSoundEvents.BOSS_ROAR.get(), 1.5F, 1.6F);
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
