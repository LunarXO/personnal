package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Veilbound.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHARD_SPARKLE =
            PARTICLES.register("shard_sparkle", () -> new SimpleParticleType(true));
}
