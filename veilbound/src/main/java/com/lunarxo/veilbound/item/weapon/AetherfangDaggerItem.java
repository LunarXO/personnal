package com.lunarxo.veilbound.item.weapon;

import com.lunarxo.veilbound.Veilbound;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class AetherfangDaggerItem extends Item {
    public AetherfangDaggerItem() {
        super(new Item.Properties().stacksTo(1).durability(400).attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Veilbound.id("aetherfang_dagger_damage"), 4.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Veilbound.id("aetherfang_dagger_speed"), -1.6D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build()));
    }
}
