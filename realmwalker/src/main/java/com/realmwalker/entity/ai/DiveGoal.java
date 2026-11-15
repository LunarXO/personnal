package com.realmwalker.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** Diving attacker: circles high above the target, then swoops down through them. */
public class DiveGoal extends Goal {
    private final Monster mob;
    private int diveCooldown = 0;

    public DiveGoal(Monster mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.mob.distanceToSqr(target) > 16.0D;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }
        if (this.diveCooldown > 0) {
            this.diveCooldown--;
            Vec3 toward = target.position().subtract(this.mob.position()).normalize();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(toward.scale(0.08D)));
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.diveCooldown == 0 && this.mob.distanceTo(target) < 4.0D) {
                this.mob.doHurtTarget(target);
            }
        } else {
            // climb above the target, then dive
            Vec3 above = target.position().add(0.0D, 8.0D, 0.0D);
            this.mob.getMoveControl().setWantedPosition(above.x, above.y, above.z, 1.2D);
            if (this.mob.distanceToSqr(target) < 100.0D && this.mob.getY() > target.getY() + 6.0D) {
                this.diveCooldown = 30;
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
