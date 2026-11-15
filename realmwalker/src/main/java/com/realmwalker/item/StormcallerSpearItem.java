package com.realmwalker.item;

import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** The Stormcaller Spear: channels a lightning strike at the block the player is looking at. */
public class StormcallerSpearItem extends Item {
    public StormcallerSpearItem(Properties properties) {
        super(properties.stacksTo(1).durability(128));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            HitResult hit = player.pick(32.0D, 0.0F, false);
            Vec3 strikePos = hit.getType() == HitResult.Type.BLOCK
                    ? Vec3.atCenterOf(((BlockHitResult) hit).getBlockPos())
                    : player.getLookAngle().scale(24.0D).add(player.getEyePosition());
            LightningBolt bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(strikePos.x, strikePos.y, strikePos.z);
                bolt.setVisualOnly(false);
                serverLevel.addFreshEntity(bolt);
            }
            level.playSound(null, player.blockPosition(), ModSoundEvents.LIGHTNING_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }
        player.getCooldowns().addCooldown(stack.getItem(), 40);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
