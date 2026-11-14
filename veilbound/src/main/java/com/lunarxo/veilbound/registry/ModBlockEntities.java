package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.block.entity.GravityAnchorBlockEntity;
import com.lunarxo.veilbound.block.entity.ResonantSpireBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Veilbound.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GravityAnchorBlockEntity>> GRAVITY_ANCHOR =
            BLOCK_ENTITY_TYPES.register("gravity_anchor", () -> BlockEntityType.Builder
                    .of(GravityAnchorBlockEntity::new, ModBlocks.GRAVITY_ANCHOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonantSpireBlockEntity>> RESONANT_SPIRE =
            BLOCK_ENTITY_TYPES.register("resonant_spire", () -> BlockEntityType.Builder
                    .of(ResonantSpireBlockEntity::new, ModBlocks.RESONANT_SPIRE.get()).build(null));
}
