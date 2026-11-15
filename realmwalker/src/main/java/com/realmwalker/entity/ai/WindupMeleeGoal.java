package com.realmwalker.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

import java.util.EnumSet;

/** Melee attack with a wind-up tell: the mob glows for a moment before the heavy strike lands. */
public class WindupMeleeGoal extends Goal {
    private final Monster mob;
    private final double speedModifier;
    private int windupTicks = -1;

    public WindupMeleeGoal(Monster mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double dist = this.mob.distanceToSqr(target);
        if (this.windupTicks < 0 && dist < 9.0D) {
            this.windupTicks = 20;
            this.mob.setAggressive(true);
        }
        if (this.windupTicks > 0) {
            this.windupTicks--;
            this.mob.getNavigation().stop();
            if (this.windupTicks == 0 && dist < 12.0D) {
                this.mob.doHurtTarget(target);
                this.mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
        } else {
            this.mob.getNavigation().moveTo(target, this.speedModifier);
        }
    }

    @Override
    public void stop() {
        this.windupTicks = -1;
        this.mob.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
