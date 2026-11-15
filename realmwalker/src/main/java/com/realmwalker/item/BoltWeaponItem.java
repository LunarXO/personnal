package com.realmwalker.item;

import com.realmwalker.entity.projectile.RealmwalkerProjectile;
import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** A one-shot bolt weapon (Clockwork Crossbow): fires a heavy bolt in the look direction. */
public class BoltWeaponItem extends Item {
    private final float damage;

    public BoltWeaponItem(Properties properties, float damage) {
        super(properties.stacksTo(1).durability(96));
        this.damage = damage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            RealmwalkerProjectile bolt = new RealmwalkerProjectile(ModEntities.MOB_BOLT.get(), serverLevel, player);
            bolt.setBoltDamage(this.damage);
            Vec3 look = player.getLookAngle();
            bolt.setPos(player.getX() + look.x, player.getEyeY() - 0.1D, player.getZ() + look.z);
            bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.2F, 0.5F);
            serverLevel.addFreshEntity(bolt);
            level.playSound(null, player.blockPosition(), ModSoundEvents.MOB_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 0.9F);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }
        player.getCooldowns().addCooldown(stack.getItem(), 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
