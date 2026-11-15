package com.realmwalker;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import com.realmwalker.registry.ModArmorMaterials;
import com.realmwalker.registry.ModBlockEntities;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModCreativeTabs;
import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModFeatures;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModMenuTypes;
import com.realmwalker.registry.ModSoundEvents;
import com.realmwalker.registry.ModTriggers;

@Mod(Realmwalker.MODID)
public class Realmwalker {
    public static final String MODID = "realmwalker";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Realmwalker(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModTriggers.TRIGGERS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, RealmwalkerConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
