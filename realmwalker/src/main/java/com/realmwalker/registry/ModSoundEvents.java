package com.realmwalker.registry;

import com.realmwalker.Realmwalker;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Realmwalker.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_OPEN = register("portal_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_TRAVEL = register("portal_travel");
    public static final DeferredHolder<SoundEvent, SoundEvent> PORTAL_AMBIENT = register("portal_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPARK_IGNITE = register("spark_ignite");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOSS_ROAR = register("boss_roar");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOSS_HURT = register("boss_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUEST_COMPLETE = register("quest_complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> CODEX_OPEN = register("codex_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_HURT = register("mob_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_DEATH = register("mob_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOB_SHOOT = register("mob_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHRINE_LOOT = register("shrine_loot");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNING_CHARGE = register("lightning_charge");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Realmwalker.id(name)));
    }

    private ModSoundEvents() {
    }
}
