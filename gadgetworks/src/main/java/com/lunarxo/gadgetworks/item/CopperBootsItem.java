package com.lunarxo.gadgetworks.item;

import com.lunarxo.gadgetworks.registry.ModArmorMaterials;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class CopperBootsItem extends ArmorItem {
    public CopperBootsItem() {
        super(ArmorItem.Type.BOOTS, ModArmorMaterials.COPPER,
                new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(9)));
    }
}
