package com.lunarxo.gadgetworks.entity;

import java.util.EnumSet;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MiniRedstoneGolem extends Mob {
    @Nullable
    private UUID ownerUUID;

    public MiniRedstoneGolem(EntityType<? extends MiniRedstoneGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 4.0F, 2.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendOwnerGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class,
                10, true, false, e -> e instanceof Enemy));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwner(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    public Player getOwner() {
        if (ownerUUID == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getPlayerByUUID(ownerUUID);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide && random.nextInt(8) == 0) {
            level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                    getX() + random.nextDouble() - 0.5D,
                    getY() + random.nextDouble() * 0.8D,
                    getZ() + random.nextDouble() - 0.5D,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity other) {
        Player owner = getOwner();
        if (owner != null && (other == owner || owner.isAlliedTo(other))) {
            return true;
        }
        return super.isAlliedTo(other);
    }

    static class FollowOwnerGoal extends Goal {
        private final MiniRedstoneGolem golem;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;

        FollowOwnerGoal(MiniRedstoneGolem golem, double speedModifier, float startDistance, float stopDistance) {
            this.golem = golem;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Player owner = golem.getOwner();
            if (owner == null || golem.getTarget() != null) {
                return false;
            }
            return golem.distanceToSqr(owner) > startDistance * startDistance;
        }

        @Override
        public boolean canContinueToUse() {
            Player owner = golem.getOwner();
            return owner != null && golem.getTarget() == null
                    && golem.distanceToSqr(owner) > stopDistance * stopDistance;
        }

        @Override
        public void tick() {
            Player owner = golem.getOwner();
            if (owner == null) {
                return;
            }
            golem.getNavigation().moveTo(owner.getX(), owner.getY(), owner.getZ(), speedModifier);
            if (golem.distanceToSqr(owner) > 144.0D && !owner.getAbilities().instabuild) {
                Vec3 pos = owner.position();
                golem.teleportTo(pos.x, pos.y, pos.z);
            }
        }

        @Override
        public void stop() {
            golem.getNavigation().stop();
        }
    }

    static class DefendOwnerGoal extends TargetGoal {
        private final MiniRedstoneGolem golem;
        @Nullable
        private LivingEntity target;

        DefendOwnerGoal(MiniRedstoneGolem golem) {
            super(golem, false);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            Player owner = golem.getOwner();
            if (owner == null) {
                return false;
            }
            LivingEntity lastHurt = owner.getLastHurtMob();
            if (lastHurt != null && lastHurt.isAlive() && lastHurt != golem && !lastHurt.isAlliedTo(golem)) {
                this.target = lastHurt;
                return true;
            }
            LivingEntity lastAttacker = owner.getLastHurtByMob();
            if (lastAttacker != null && lastAttacker.isAlive() && lastAttacker != golem) {
                this.target = lastAttacker;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            golem.setTarget(target);
            super.start();
        }

        @Override
        public void stop() {
            target = null;
            super.stop();
        }
    }
}
