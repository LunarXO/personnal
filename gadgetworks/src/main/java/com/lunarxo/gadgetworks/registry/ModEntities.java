package com.lunarxo.gadgetworks.registry;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.entity.MiniRedstoneGolem;
import com.lunarxo.gadgetworks.entity.RedstoneSpark;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Gadgetworks.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MiniRedstoneGolem>> MINI_REDSTONE_GOLEM = ENTITIES.register("mini_redstone_golem",
            () -> EntityType.Builder.<MiniRedstoneGolem>of(MiniRedstoneGolem::new, MobCategory.MISC)
                    .sized(0.5F, 0.9F).clientTrackingRange(10).build("mini_redstone_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<RedstoneSpark>> REDSTONE_SPARK = ENTITIES.register("redstone_spark",
            () -> EntityType.Builder.<RedstoneSpark>of(RedstoneSpark::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(2).build("redstone_spark"));
}
