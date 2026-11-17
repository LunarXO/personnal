package com.lunarxo.gadgetworks.event;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.entity.MiniRedstoneGolem;
import com.lunarxo.gadgetworks.registry.ModEntities;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Gadgetworks.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MINI_REDSTONE_GOLEM.get(), MiniRedstoneGolem.createAttributes().build());
    }
}
