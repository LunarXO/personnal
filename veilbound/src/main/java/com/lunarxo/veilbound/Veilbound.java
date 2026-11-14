package com.lunarxo.veilbound;

import org.slf4j.Logger;

import com.lunarxo.veilbound.registry.ModArmorMaterials;
import com.lunarxo.veilbound.registry.ModBlockEntities;
import com.lunarxo.veilbound.registry.ModBlocks;
import com.lunarxo.veilbound.registry.ModCreativeTabs;
import com.lunarxo.veilbound.registry.ModEffects;
import com.lunarxo.veilbound.registry.ModEntities;
import com.lunarxo.veilbound.registry.ModItems;
import com.lunarxo.veilbound.registry.ModParticles;
import com.lunarxo.veilbound.registry.ModSoundEvents;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Veilbound.MODID)
public class Veilbound {
    public static final String MODID = "veilbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Veilbound(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, VeilboundConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
