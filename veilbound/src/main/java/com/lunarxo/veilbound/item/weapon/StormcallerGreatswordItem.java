package com.lunarxo.veilbound.item.weapon;

import com.lunarxo.veilbound.registry.ModParticles;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class StormcallerGreatswordItem extends SwordItem {
    public StormcallerGreatswordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel serverLevel) {
            DamageSource source = attacker instanceof Mob mob
                    ? attacker.damageSources().mobAttack(mob)
                    : attacker.damageSources().generic();
            for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class,
                    target.getBoundingBox().inflate(2.5D), e -> e != attacker && e != target && e.isAlive())) {
                nearby.hurt(source, 5.0F);
                nearby.knockback(0.9D, attacker.getX() - nearby.getX(), attacker.getZ() - nearby.getZ());
            }
            serverLevel.sendParticles((ParticleOptions) ModParticles.SHARD_SPARKLE.get(), target.getX(),
                    target.getY() + 1.0D, target.getZ(), 25, 1.2D, 0.8D, 1.2D, 0.05D);
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSoundEvents.SHARD_SHOOT.get(), SoundSource.PLAYERS, 0.8F, 0.6F);
        }
        super.postHurtEnemy(stack, target, attacker);
    }
}
