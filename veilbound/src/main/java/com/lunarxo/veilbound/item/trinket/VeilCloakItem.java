package com.lunarxo.veilbound.item.trinket;

import com.lunarxo.veilbound.ModDimensions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VeilCloakItem extends Item {
    public VeilCloakItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) {
            return;
        }
        if (!level.dimension().equals(ModDimensions.THE_VEIL)) {
            return;
        }
        Vec3 movement = living.getDeltaMovement();
        boolean still = movement.x * movement.x + movement.z * movement.z < 1.0E-7D;
        if (still) {
            living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false, false));
        }
    }
}
