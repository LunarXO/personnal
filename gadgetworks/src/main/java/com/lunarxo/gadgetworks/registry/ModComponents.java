package com.lunarxo.gadgetworks.registry;

import com.lunarxo.gadgetworks.Gadgetworks;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Gadgetworks.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MAGNETIC_SIDE =
            COMPONENTS.register("magnetic_side", () -> DataComponentType.<Boolean>builder()
                    .persistent(com.mojang.serialization.Codec.BOOL)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<net.minecraft.core.BlockPos>> GRAPPLE_TARGET =
            COMPONENTS.register("grapple_target", () -> DataComponentType.<net.minecraft.core.BlockPos>builder()
                    .persistent(net.minecraft.core.BlockPos.CODEC)
                    .networkSynchronized(net.minecraft.core.BlockPos.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STAFF_MODE =
            COMPONENTS.register("staff_mode", () -> DataComponentType.<Integer>builder()
                    .persistent(com.mojang.serialization.Codec.intRange(0, 3))
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT)
                    .build());
}
