package com.realmwalker.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** A consumable that applies a status effect (or heals) on use, like a potion. */
public class ConsumableBuffItem extends Item {
    private final MobEffectInstance effect;
    private final float healAmount;

    public ConsumableBuffItem(Properties properties, MobEffectInstance effect, float healAmount) {
        super(properties);
        this.effect = effect;
        this.healAmount = healAmount;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return net.minecraft.world.item.ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            if (this.healAmount > 0.0F) {
                entity.heal(this.healAmount);
            }
            if (this.effect != null) {
                entity.addEffect(new MobEffectInstance(this.effect));
            }
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }
}
