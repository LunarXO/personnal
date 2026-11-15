package com.realmwalker.registry;

import com.realmwalker.Realmwalker;
import com.realmwalker.gui.CodexMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU_TYPE, Realmwalker.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CodexMenu>> CODEX =
            MENU_TYPES.register("codex", () -> IMenuTypeExtension.create(CodexMenu::new));

    private ModMenuTypes() {
    }
}
