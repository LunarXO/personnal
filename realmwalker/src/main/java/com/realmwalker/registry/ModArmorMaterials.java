package com.realmwalker.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.realmwalker.Realmwalker;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Realmwalker.MODID);


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MIREWEAVE = ARMOR_MATERIALS.register("mireweave",
            () -> new ArmorMaterial(defense(2, 6, 8, 3), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CAPLING_CAP = ARMOR_MATERIALS.register("capling_cap",
            () -> new ArmorMaterial(defense(1, 2, 3, 1), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MIRAGE = ARMOR_MATERIALS.register("mirage",
            () -> new ArmorMaterial(defense(2, 5, 7, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DUNE = ARMOR_MATERIALS.register("dune",
            () -> new ArmorMaterial(defense(2, 3, 5, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> COG = ARMOR_MATERIALS.register("cog",
            () -> new ArmorMaterial(defense(3, 6, 8, 3), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PARKA = ARMOR_MATERIALS.register("parka",
            () -> new ArmorMaterial(defense(3, 3, 6, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ICECLEATS = ARMOR_MATERIALS.register("icecleats",
            () -> new ArmorMaterial(defense(2, 2, 4, 1), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GALE = ARMOR_MATERIALS.register("gale",
            () -> new ArmorMaterial(defense(2, 4, 6, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CAPE = ARMOR_MATERIALS.register("cape",
            () -> new ArmorMaterial(defense(2, 5, 7, 2), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RODKNIGHT = ARMOR_MATERIALS.register("rodknight",
            () -> new ArmorMaterial(defense(3, 7, 9, 3), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> WAYFARER = ARMOR_MATERIALS.register("wayfarer",
            () -> new ArmorMaterial(defense(4, 8, 10, 4), 12, SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(ModItems.WAYSTONE_SHARD.get()),
                    List.of(new ArmorMaterial.Layer(Realmwalker.id(mat))), 1.0F, 0.0F));


    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        return map;
    }

    private ModArmorMaterials() {
    }
}
