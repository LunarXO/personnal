package com.lunarxo.gadgetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.List;

public class GrapplingHookItem extends Item {
    private static final double MAX_RANGE = 32.0D;

    public GrapplingHookItem() {
        super(new Item.Properties().durability(256).stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = player.pick(MAX_RANGE, 1.0F, false);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.fail(stack);
        }

        Vec3 target = hit.getLocation();
        Vec3 toTarget = target.subtract(player.getEyePosition());
        double distance = toTarget.length();
        Vec3 direction = toTarget.normalize();

        double speed = Math.min(1.6D, 0.45D + distance * 0.05D);
        Vec3 launch = direction.scale(speed).add(0.0D, 0.25D, 0.0D);
        player.setDeltaMovement(launch);
        player.resetFallDistance();
        player.getCooldowns().addCooldown(this, 20);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 1.0F, 0.8F);
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                Vec3 from = player.getEyePosition();
                int steps = (int) (distance / 0.5D);
                for (int i = 0; i < steps; i++) {
                    Vec3 p = from.add(toTarget.scale(i / (double) steps));
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT,
                            p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.grappling_hook").withStyle(ChatFormatting.GRAY));
    }
}
