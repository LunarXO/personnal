package com.lunarxo.gadgetworks.item;

import java.util.List;

import com.lunarxo.gadgetworks.registry.ModComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagneticDoubleSwordItem extends SwordItem {
    private static final double FIELD_RANGE = 6.0D;
    private static final double ACTIVE_RANGE = 8.0D;

    public MagneticDoubleSwordItem() {
        super(Tiers.IRON, new Item.Properties().durability(750));
    }

    public static boolean isNorthSide(ItemStack stack) {
        return !stack.has(ModComponents.MAGNETIC_SIDE.get())
                || Boolean.TRUE.equals(stack.get(ModComponents.MAGNETIC_SIDE.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            boolean north = !isNorthSide(stack);
            stack.set(ModComponents.MAGNETIC_SIDE.get(), north);
            player.displayClientMessage(Component.translatable(north
                    ? "message.gadgetworks.magnetic_north"
                    : "message.gadgetworks.magnetic_south"), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, north ? 1.2F : 0.7F);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        player.getCooldowns().addCooldown(this, 30);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            boolean north = isNorthSide(stack);
            List<Mob> nearby = serverLevel.getEntitiesOfClass(Mob.class,
                    player.getBoundingBox().inflate(ACTIVE_RANGE),
                    e -> e.isAlive() && !e.isAlliedTo(player));

            for (Mob mob : nearby) {
                if (north) {
                    Vec3 toPlayer = player.position().subtract(mob.position());
                    mob.addDeltaMovement(toPlayer.normalize().scale(1.2D).add(0.0D, 0.15D, 0.0D));
                } else {
                    Vec3 away = mob.position().subtract(player.position());
                    mob.addDeltaMovement(away.normalize().scale(1.4D).add(0.0D, 0.35D, 0.0D));
                }
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                        4, 0.1D, 0.1D, 0.1D, 0.05D);
            }

            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.8F, north ? 1.3F : 0.6F);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (attacker.level().isClientSide || !(attacker.level() instanceof ServerLevel level)) {
            return;
        }

        boolean north = isNorthSide(stack);
        List<Mob> nearby = level.getEntitiesOfClass(Mob.class,
                attacker.getBoundingBox().inflate(FIELD_RANGE),
                e -> e != target && e != attacker && e.isAlive());

        for (Mob mob : nearby) {
            Vec3 delta = attacker.position().subtract(mob.position());
            if (north) {
                mob.addDeltaMovement(delta.normalize().scale(0.8D).add(0.0D, 0.1D, 0.0D));
            } else {
                Vec3 away = mob.position().subtract(attacker.position());
                mob.addDeltaMovement(away.normalize().scale(1.1D).add(0.0D, 0.3D, 0.0D));
            }
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5D, mob.getZ(),
                    3, 0.1D, 0.1D, 0.1D, 0.02D);
        }

        if (!nearby.isEmpty()) {
            level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.6F, north ? 1.3F : 0.6F);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.magnetic_double_sword").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(isNorthSide(stack)
                ? "tooltip.gadgetworks.magnetic_side_north"
                : "tooltip.gadgetworks.magnetic_side_south").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.gadgetworks.magnetic_double_sword_active").withStyle(ChatFormatting.GRAY));
    }
}
