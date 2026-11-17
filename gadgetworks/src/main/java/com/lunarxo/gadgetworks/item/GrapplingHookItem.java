package com.lunarxo.gadgetworks.item;

import java.util.List;

import com.lunarxo.gadgetworks.registry.ModComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookItem extends Item {
    private static final double MAX_RANGE = 32.0D;

    public GrapplingHookItem() {
        super(new Item.Properties().durability(256).stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = player.pick(MAX_RANGE, 1.0F, false);
        if (hit.getType() == HitResult.Type.MISS || !(hit instanceof net.minecraft.world.phys.BlockHitResult blockHit)) {
            return InteractionResultHolder.fail(stack);
        }

        BlockPos target = blockHit.getBlockPos();
        stack.set(ModComponents.GRAPPLE_TARGET.get(), target);
        player.startUsingItem(hand);
        player.awardStat(Stats.ITEM_USED.get(this));

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 1.0F, 0.8F);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTick) {
        BlockPos target = stack.get(ModComponents.GRAPPLE_TARGET.get());
        if (target == null) {
            entity.stopUsingItem();
            return;
        }

        Vec3 center = Vec3.atCenterOf(target);
        Vec3 toTarget = center.subtract(entity.position());
        double distance = toTarget.length();

        if (distance < 1.5D) {
            finishPull(entity, stack);
            return;
        }

        Vec3 pull = toTarget.normalize().scale(Math.min(1.1D, 0.35D + distance * 0.04D));
        entity.setDeltaMovement(pull);
        entity.resetFallDistance();

        if (!level.isClientSide && level instanceof ServerLevel serverLevel
                && entity.tickCount % 2 == 0) {
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    entity.getX() + pull.x, entity.getY() + 0.5D, entity.getZ() + pull.z,
                    2, 0.1D, 0.1D, 0.1D, 0.0D);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        finishPull(entity, stack);
    }

    private void finishPull(LivingEntity entity, ItemStack stack) {
        stack.remove(ModComponents.GRAPPLE_TARGET.get());
        entity.stopUsingItem();
        entity.resetFallDistance();
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 15);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.grappling_hook").withStyle(ChatFormatting.GRAY));
    }
}
