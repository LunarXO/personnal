package com.lunarxo.gadgetworks.item;

import java.util.List;

import com.lunarxo.gadgetworks.Gadgetworks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class GroundBreakingSpearItem extends SwordItem {
    private static final double SLAM_RADIUS = 3.5D;
    private static final float SLAM_DAMAGE = 5.0F;

    public GroundBreakingSpearItem() {
        super(Tiers.IRON, new Item.Properties().durability(1024).attributes(
                SwordItem.createAttributes(Tiers.IRON, 4, -2.8F)
                        .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,
                                new AttributeModifier(Gadgetworks.id("spear_reach"), 2.0D,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.onGround()) {
            return InteractionResultHolder.pass(stack);
        }

        player.getCooldowns().addCooldown(this, 100);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            List<Mob> targets = serverLevel.getEntitiesOfClass(Mob.class,
                    player.getBoundingBox().inflate(SLAM_RADIUS),
                    e -> !e.is(player) && e.isAlive());
            for (Mob mob : targets) {
                mob.hurt(player.damageSources().playerAttack(player), SLAM_DAMAGE);
                Vec3 away = mob.position().subtract(player.position());
                mob.addDeltaMovement(away.normalize().scale(0.6D).add(0.0D, 0.8D, 0.0D));
            }

            for (int i = 0; i < 24; i++) {
                double angle = (Math.PI * 2 * i) / 24;
                serverLevel.sendParticles(ParticleTypes.POOF,
                        player.getX() + Math.cos(angle) * SLAM_RADIUS * 0.7D,
                        player.getY() + 0.2D,
                        player.getZ() + Math.sin(angle) * SLAM_RADIUS * 0.7D,
                        3, 0.1D, 0.1D, 0.1D, 0.0D);
            }
            serverLevel.sendParticles((ParticleOptions) new DustParticleOptions(new Vector3f(0.6F, 0.4F, 0.2F), 1.5F),
                    player.getX(), player.getY() + 0.3D, player.getZ(),
                    40, SLAM_RADIUS * 0.5D, 0.2D, SLAM_RADIUS * 0.5D, 0.0D);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.7F, 1.2F);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 1.0F, 0.7F);
            stack.hurtAndBreak(3, player, LivingEntity.getSlotForHand(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.gadgetworks.ground_breaking_spear").withStyle(ChatFormatting.GRAY));
    }
}
