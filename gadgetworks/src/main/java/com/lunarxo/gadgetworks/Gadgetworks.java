package com.lunarxo.gadgetworks;

import com.lunarxo.gadgetworks.registry.ModArmorMaterials;
import com.lunarxo.gadgetworks.registry.ModComponents;
import com.lunarxo.gadgetworks.registry.ModCreativeTabs;
import com.lunarxo.gadgetworks.registry.ModEntities;
import com.lunarxo.gadgetworks.registry.ModItems;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Gadgetworks.MODID)
public class Gadgetworks {
    public static final String MODID = "gadgetworks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Gadgetworks(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModComponents.COMPONENTS.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
