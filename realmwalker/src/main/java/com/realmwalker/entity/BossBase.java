package com.realmwalker.entity;

import com.realmwalker.RealmwalkerConfig;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class BossBase extends Monster {
    private final ServerBossEvent bossEvent;
    private int roarCooldown = 0;

    protected BossBase(EntityType<? extends Monster> type, Level level, String nameKey, BossEvent.BossBarColor color) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(Component.translatable(nameKey), color, BossEvent.BossBarOverlay.PROGRESS);
        this.xpReward = 100;
    }

    public static AttributeSupplier.Builder bossAttributes(double health, double damage, double speed) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, health * RealmwalkerConfig.bossHealthMultiplier)
                .add(Attributes.ATTACK_DAMAGE, damage)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    protected abstract int getPhaseCount();

    public int getPhase() {
        float ratio = this.getHealth() / this.getMaxHealth();
        int phases = getPhaseCount();
        int phase = phases;
        for (int i = 1; i <= phases; i++) {
            if (ratio > phaseThreshold(i, phases)) {
                phase = i;
                break;
            }
        }
        return phase;
    }

    private static float phaseThreshold(int phase, int phases) {
        return 1.0F - (float) (phase - 1) / phases;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            if (this.roarCooldown > 0) {
                this.roarCooldown--;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide && this.roarCooldown == 0 && this.getHealth() > 0) {
            this.level().playSound(null, this.blockPosition(), ModSoundEvents.BOSS_HURT.get(), SoundSource.HOSTILE, 1.5F, 0.7F);
            this.roarCooldown = 40;
        }
        return result;
    }

    public void roar() {
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.blockPosition(), ModSoundEvents.BOSS_ROAR.get(), SoundSource.HOSTILE, 2.0F, 0.6F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("roar_cooldown", this.roarCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.roarCooldown = tag.getInt("roar_cooldown");
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
