package com.lunarxo.gadgetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ElectricSwordItem extends SwordItem {
    private static final double CHAIN_RANGE = 5.0D;
    private static final int CHAIN_TARGETS = 3;
    private static final float CHAIN_DAMAGE = 3.0F;

    public ElectricSwordItem() {
        super(Tiers.DIAMOND, new Item.Properties().durability(1561));
    }

    @Override
    public void postHurtEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEntity(stack, target, attacker);
        if (attacker.level().isClientSide || !(attacker.level() instanceof ServerLevel level)) {
            return;
        }

        List<Mob> nearby = level.getEntitiesOfClass(Mob.class,
                target.getBoundingBox().inflate(CHAIN_RANGE),
                e -> e != target && e != attacker && e.isAlive() && !e.isAlliedTo(attacker));
        int zapped = 0;
        for (Mob mob : nearby) {
            if (zapped >= CHAIN_TARGETS) {
                break;
            }
            zapped++;
            mob.hurt(attacker.damageSources().mobAttack(attacker), CHAIN_DAMAGE);
            drawSpark(level, target, mob);
        }

        if (zapped > 0) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    12, 0.3D, 0.3D, 0.3D, 0.1D);
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.LIGHTNING_IMPACT, SoundSource.PLAYERS, 0.5F, 1.4F);
            stack.hurtAndBreak(1, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
        }
    }

    private void drawSpark(ServerLevel level, LivingEntity from, LivingEntity to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() + to.getBbHeight() * 0.5D - (from.getY() + from.getBbHeight() * 0.5D);
        double dz = to.getZ() - from.getZ();
        int steps = 8;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    from.getX() + dx * t, from.getY() + from.getBbHeight() * 0.5D + dy * t, from.getZ() + dz * t,
                    2, 0.05D, 0.05D, 0.05D, 0.0D);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.electric_sword").withStyle(ChatFormatting.AQUA));
    }
}
