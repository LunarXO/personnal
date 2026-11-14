package com.lunarxo.veilbound.registry;

import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import java.util.function.Supplier;

import com.lunarxo.veilbound.Veilbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Veilbound.MODID);

    public static final int EMBER_DURABILITY = 24;
    public static final int AETHERITE_DURABILITY = 30;
    public static final int VOIDSTEEL_DURABILITY = 38;

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> EMBER = ARMOR_MATERIALS.register("ember",
            () -> new ArmorMaterial(defense(2, 5, 6, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.EMBER_CORE.get()),
                    List.of(new ArmorMaterial.Layer(Veilbound.id("ember"))), 0.0F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AETHERITE = ARMOR_MATERIALS.register("aetherite",
            () -> new ArmorMaterial(defense(3, 6, 8, 3), 18, SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(ModItems.AETHERITE_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Veilbound.id("aetherite"))), 1.5F, 0.0F));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOIDSTEEL = ARMOR_MATERIALS.register("voidsteel",
            () -> new ArmorMaterial(defense(3, 7, 9, 3), 22, SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(ModItems.VOIDSTONE_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(Veilbound.id("voidsteel"))), 3.0F, 0.1F));

    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        return map;
    }
}
