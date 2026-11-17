package com.lunarxo.gadgetworks.registry;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.item.CopperBootsItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gadgetworks.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GADGETWORKS_TAB = CREATIVE_MODE_TABS.register("gadgetworks_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gadgetworks.gadgetworks_tab"))
                    .icon(() -> ModItems.GRAPPLING_HOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> ModItems.ITEMS.getEntries().forEach(entry -> output.accept(entry.get())))
                    .build());
}
