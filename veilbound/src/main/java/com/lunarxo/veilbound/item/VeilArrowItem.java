package com.lunarxo.veilbound.item;

import com.lunarxo.veilbound.entity.projectile.VeilArrowEntity;
import com.lunarxo.veilbound.registry.ModEntities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VeilArrowItem extends ArrowItem {
    public VeilArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammoStack, LivingEntity shooter, ItemStack weaponStack) {
        VeilArrowEntity arrow = new VeilArrowEntity(ModEntities.VEIL_ARROW.get(), level);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        arrow.setOwner(shooter);
        arrow.setBaseDamage(4.5D);
        return arrow;
    }
}
