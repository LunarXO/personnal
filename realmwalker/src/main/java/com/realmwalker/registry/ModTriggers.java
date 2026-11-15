package com.realmwalker.registry;

import com.realmwalker.Realmwalker;
import com.realmwalker.trigger.GenericTrigger;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPES, Realmwalker.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, GenericTrigger> GENERIC = TRIGGERS.register("generic", GenericTrigger::new);

    private ModTriggers() {
    }
}
