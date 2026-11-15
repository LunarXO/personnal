package com.realmwalker.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;

/** Armor that applies a passive effect while worn (Dune Boots, Ice Cleats, Gale Boots, Cloud Cape...). */
public class ArmorEffectItem extends ArmorItem {
    private final MobEffectInstance effect;

    public ArmorEffectItem(Holder<ArmorMaterial> material, Type type, Properties properties, MobEffectInstance effect) {
        super(material, type, properties);
        this.effect = effect;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof LivingEntity living && stack.getEquipmentSlot() == this.getType().getSlot()) {
            living.addEffect(new MobEffectInstance(this.effect.getEffect(), 40, this.effect.getAmplifier(), false, false, false));
        }
    }
}
