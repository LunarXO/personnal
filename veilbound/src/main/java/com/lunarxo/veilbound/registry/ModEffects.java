package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.effect.EmberMarkEffect;
import com.lunarxo.veilbound.effect.GravityWellEffect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Veilbound.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> EMBER_MARK =
            MOB_EFFECTS.register("ember_mark", () -> new EmberMarkEffect(MobEffectCategory.HARMFUL, 0xFF5500));

    public static final DeferredHolder<MobEffect, MobEffect> GRAVITY_WELL =
            MOB_EFFECTS.register("gravity_well", () -> new GravityWellEffect(MobEffectCategory.BENEFICIAL, 0x8A2BE2));
}
