package com.lunarxo.gadgetworks.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FrostbrandAxeItem extends AxeItem {
    private static final double NOVA_RADIUS = 5.0D;
    private static final float NOVA_DAMAGE = 3.0F;

    public FrostbrandAxeItem() {
        super(Tiers.IRON, new Item.Properties().durability(850));
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 100);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class,
                    player.getBoundingBox().inflate(NOVA_RADIUS),
                    e -> !e.is(player) && e.isAlive());
            for (Mob mob : targets) {
                mob.hurt(player.damageSources().playerAttack(player), NOVA_DAMAGE);
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
            }

            for (int i = 0; i < 32; i++) {
                double angle = (Math.PI * 2 * i) / 32;
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        player.getX() + Math.cos(angle) * NOVA_RADIUS * 0.6D,
                        player.getY() + 0.5D,
                        player.getZ() + Math.sin(angle) * NOVA_RADIUS * 0.6D,
                        3, 0.05D, 0.1D, 0.05D, 0.0D);
            }
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY() + 1.0D, player.getZ(),
                    30, NOVA_RADIUS * 0.4D, 0.5D, NOVA_RADIUS * 0.4D, 0.02D);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 0.6F, 1.0F);
            stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.frostbrand_axe").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.gadgetworks.frostbrand_axe_active").withStyle(ChatFormatting.BLUE));
    }
}
