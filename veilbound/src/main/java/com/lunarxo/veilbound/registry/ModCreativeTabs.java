package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Veilbound.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VEILBOUND_TAB = CREATIVE_MODE_TABS.register("veilbound_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.veilbound.veilbound_tab"))
                    .icon(() -> ModItems.VEIL_KEY.get().getDefaultInstance())
                    .displayItems((parameters, output) -> ModItems.ITEMS.getEntries().forEach(entry -> output.accept(entry.get())))
                    .build());
}
