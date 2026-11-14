package com.lunarxo.veilbound.item.weapon;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ChargeWeaponItem extends Item {
    public ChargeWeaponItem(Properties properties) {
        super(properties.stacksTo(1).durability(320));
    }

    protected abstract Projectile createProjectile(Level level, LivingEntity shooter);

    protected abstract SoundEvent getShootSound();

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
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int charge = getUseDuration(stack, entity) - timeLeft;
        float power = getPowerForTime(charge);
        if (power < 0.1F || level.isClientSide) {
            return;
        }
        Projectile projectile = createProjectile(level, entity);
        projectile.setPos(entity.getX(), entity.getEyeY() - 0.1D, entity.getZ());
        Vec3 look = entity.getLookAngle();
        projectile.shoot(look.x, look.y, look.z, power * 2.5F, 1.0F);
        level.addFreshEntity(projectile);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), getShootSound(), SoundSource.PLAYERS, 1.0F,
                0.9F + power * 0.2F);
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
    }

    public static float getPowerForTime(int charge) {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }
}
