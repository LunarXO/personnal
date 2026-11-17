package com.lunarxo.gadgetworks.item;

import java.util.List;

import com.lunarxo.gadgetworks.entity.MiniRedstoneGolem;
import com.lunarxo.gadgetworks.entity.RedstoneSpark;
import com.lunarxo.gadgetworks.registry.ModComponents;
import com.lunarxo.gadgetworks.registry.ModEntities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class RedstoneStaffItem extends Item {
    public static final int MODE_SUMMON = 0;
    public static final int MODE_RECALL = 1;
    public static final int MODE_ZAP = 2;
    public static final int MODE_PULSE = 3;
    private static final int MODE_COUNT = 4;
    private static final int MAX_GOLEMS = 3;
    private static final double PULSE_RADIUS = 5.0D;

    public RedstoneStaffItem() {
        super(new Item.Properties().durability(512).stacksTo(1));
    }

    public static int getMode(ItemStack stack) {
        Integer mode = stack.get(ModComponents.STAFF_MODE.get());
        return mode == null ? MODE_SUMMON : mode;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean client = level.isClientSide;

        if (player.isShiftKeyDown()) {
            int mode = (getMode(stack) + 1) % MODE_COUNT;
            stack.set(ModComponents.STAFF_MODE.get(), mode);
            player.displayClientMessage(Component.translatable(modeName(mode)), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 0.8F, 0.6F + mode * 0.2F);
            return InteractionResultHolder.sidedSuccess(stack, client);
        }

        int mode = getMode(stack);
        return switch (mode) {
            case MODE_SUMMON -> summon(level, player, hand, stack, client);
            case MODE_RECALL -> recall(level, player, hand, stack, client);
            case MODE_ZAP -> zap(level, player, hand, stack, client);
            case MODE_PULSE -> pulse(level, player, hand, stack, client);
            default -> InteractionResultHolder.pass(stack);
        };
    }

    private InteractionResultHolder<ItemStack> summon(Level level, Player player, InteractionHand hand, ItemStack stack, boolean client) {
        if (!client && level instanceof ServerLevel serverLevel) {
            List<MiniRedstoneGolem> owned = getOwnedGolems(serverLevel, player);
            if (owned.size() >= MAX_GOLEMS) {
                player.displayClientMessage(Component.translatable("message.gadgetworks.golem_limit"), true);
                return InteractionResultHolder.fail(stack);
            }

            Vec3 look = player.getLookAngle();
            double x = player.getX() + look.x * 2.0D;
            double z = player.getZ() + look.z * 2.0D;
            MiniRedstoneGolem golem = ModEntities.MINI_REDSTONE_GOLEM.get().create(serverLevel);
            if (golem != null) {
                golem.moveTo(x, player.getY(), z, player.getYRot(), 0.0F);
                golem.setOwner(player.getUUID());
                golem.setPersistenceRequired();
                serverLevel.addFreshEntity(golem);
                serverLevel.sendParticles(new DustParticleOptions(new Vector3f(0.8F, 0.1F, 0.1F), 1.0F),
                        x, player.getY() + 0.5D, z, 20, 0.3D, 0.5D, 0.3D, 0.0D);
                serverLevel.playSound(null, x, player.getY(), z,
                        SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.8F, 1.4F);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                player.getCooldowns().addCooldown(this, 40);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, client);
    }

    private InteractionResultHolder<ItemStack> recall(Level level, Player player, InteractionHand hand, ItemStack stack, boolean client) {
        if (!client && level instanceof ServerLevel serverLevel) {
            List<MiniRedstoneGolem> owned = getOwnedGolems(serverLevel, player);
            for (MiniRedstoneGolem golem : owned) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,
                        golem.getX(), golem.getY() + 0.4D, golem.getZ(), 10, 0.2D, 0.2D, 0.2D, 0.0D);
                golem.discard();
            }
            if (!owned.isEmpty()) {
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.PLAYERS, 0.8F, 1.5F);
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, client);
    }

    private InteractionResultHolder<ItemStack> zap(Level level, Player player, InteractionHand hand, ItemStack stack, boolean client) {
        player.getCooldowns().addCooldown(this, 15);
        if (!client) {
            RedstoneSpark spark = new RedstoneSpark(ModEntities.REDSTONE_SPARK.get(), player, level);
            Vec3 look = player.getLookAngle();
            spark.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
            spark.shoot(look.x, look.y, look.z, 1.8F, 1.0F);
            level.addFreshEntity(spark);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.6F);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, client);
    }

    private InteractionResultHolder<ItemStack> pulse(Level level, Player player, InteractionHand hand, ItemStack stack, boolean client) {
        player.getCooldowns().addCooldown(this, 60);
        if (!client && level instanceof ServerLevel serverLevel) {
            List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class,
                    player.getBoundingBox().inflate(PULSE_RADIUS),
                    e -> e != player && e.isAlive());
            for (Mob mob : targets) {
                mob.hurt(player.damageSources().playerAttack(player), 4.0F);
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4));
            }
            for (int i = 0; i < 32; i++) {
                double angle = (Math.PI * 2 * i) / 32;
                serverLevel.sendParticles(new DustParticleOptions(new Vector3f(0.9F, 0.15F, 0.15F), 1.2F),
                        player.getX() + Math.cos(angle) * PULSE_RADIUS * 0.6D,
                        player.getY() + 0.4D,
                        player.getZ() + Math.sin(angle) * PULSE_RADIUS * 0.6D,
                        4, 0.05D, 0.1D, 0.05D, 0.0D);
            }
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5F, 1.6F);
            stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, client);
    }

    public static List<MiniRedstoneGolem> getOwnedGolems(ServerLevel level, Player player) {
        return level.getEntitiesOfClass(MiniRedstoneGolem.class,
                player.getBoundingBox().inflate(64.0D),
                golem -> player.getUUID().equals(golem.getOwnerUUID()));
    }

    public static String modeName(int mode) {
        return switch (mode) {
            case MODE_RECALL -> "message.gadgetworks.staff_mode_recall";
            case MODE_ZAP -> "message.gadgetworks.staff_mode_zap";
            case MODE_PULSE -> "message.gadgetworks.staff_mode_pulse";
            default -> "message.gadgetworks.staff_mode_summon";
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.redstone_staff").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(modeName(getMode(stack))).withStyle(ChatFormatting.RED));
    }
}
