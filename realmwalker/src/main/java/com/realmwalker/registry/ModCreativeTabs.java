package com.realmwalker.registry;

import com.realmwalker.Realmwalker;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Realmwalker.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REALMWALKER_TAB = CREATIVE_MODE_TABS.register("realmwalker_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.realmwalker.realmwalker_tab"))
                    .icon(() -> ModItems.WAYFARERS_CODEX.get().getDefaultInstance())
                    .displayItems((parameters, output) -> ModItems.ITEMS.getEntries().forEach(entry -> output.accept(entry.get())))
                    .build());

    private ModCreativeTabs() {
    }
}
