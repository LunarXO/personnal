package com.realmwalker.item.stormspire;

import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** The Lightning Bottle: a consumable that calls down a lightning strike where the player looks. */
public class LightningBottleItem extends Item {
    public LightningBottleItem(Properties properties) {
        super(properties.stacksTo(8));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            HitResult hit = player.pick(32.0D, 0.0F, false);
            Vec3 pos = hit.getType() == HitResult.Type.BLOCK
                    ? Vec3.atCenterOf(((BlockHitResult) hit).getBlockPos())
                    : player.getLookAngle().scale(16.0D).add(player.getEyePosition());
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(pos.x, pos.y, pos.z);
                serverLevel.addFreshEntity(bolt);
            }
            level.playSound(null, player.blockPosition(), ModSoundEvents.LIGHTNING_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 0.7F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
