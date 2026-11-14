package com.lunarxo.veilbound.entity;

import com.lunarxo.veilbound.ModDimensions;
import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.VeilboundConfig;
import com.lunarxo.veilbound.entity.projectile.GloomOrbEntity;
import com.lunarxo.veilbound.registry.ModEntities;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class HollowKingEntity extends Monster implements RangedAttackMob {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity." + Veilbound.MODID + ".hollow_king"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public HollowKingEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, VeilboundConfig.hollowKingHealth)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    public static boolean checkVeilSpawn(EntityType<? extends HollowKingEntity> type, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getLevel().dimension() == ModDimensions.THE_VEIL
                && Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public int getPhase() {
        float ratio = this.getHealth() / this.getMaxHealth();
        if (ratio > 0.66F) {
            return 1;
        }
        return ratio > 0.33F ? 2 : 3;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, getPhase() >= 3 ? 1.4D : 1.0D, false));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.0D, 60, 20.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.getPhase() >= 2 && this.tickCount % 120 == 0) {
            for (int i = 0; i < 2; i++) {
                EmberWispEntity wisp = ModEntities.EMBER_WISP.get().create(this.level());
                if (wisp != null) {
                    wisp.moveTo(this.getX() + this.random.nextGaussian() * 3.0D, this.getY() + 1.0D,
                            this.getZ() + this.random.nextGaussian() * 3.0D, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(wisp);
                }
            }
        }
        if (this.getPhase() == 3 && this.tickCount % 200 == 0) {
            this.playSound(ModSoundEvents.BOSS_ROAR.get(), 2.0F, 0.6F);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        for (int i = -1; i <= 1; i++) {
            GloomOrbEntity orb = new GloomOrbEntity(ModEntities.GLOOM_ORB.get(), this, level);
            orb.setPos(this.getX(), this.getEyeY() - 0.1D, this.getZ());
            double dx = target.getX() - this.getX();
            double dy = target.getY() + target.getBbHeight() * 0.5D - orb.getY();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            orb.shoot(dx + i * 0.6D, dy + dist * 0.05D, dz - i * 0.6D, 1.6F, 3.0F);
            level.addFreshEntity(orb);
        }
        this.playSound(ModSoundEvents.BOSS_ROAR.get(), 1.5F, 0.8F);
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
