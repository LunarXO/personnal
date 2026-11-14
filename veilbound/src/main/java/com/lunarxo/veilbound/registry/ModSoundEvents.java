package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Veilbound.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_OPEN = register("portal_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_TRAVEL = register("portal_travel");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_AMBIENT = register("portal_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHARD_SHOOT = register("shard_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLOOM_CAST = register("gloom_cast");
    public static final DeferredHolder<SoundEvent, SoundEvent> WISP_SHOOT = register("wisp_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOSS_ROAR = register("boss_roar");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Veilbound.id(name)));
    }
}
