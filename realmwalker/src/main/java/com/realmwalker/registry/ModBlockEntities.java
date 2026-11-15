package com.realmwalker.registry;

import com.realmwalker.Realmwalker;
import com.realmwalker.block.entity.WaystonePortalBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Realmwalker.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaystonePortalBlockEntity>> WAYSTONE_PORTAL =
            BLOCK_ENTITY_TYPES.register("waystone_portal", () -> BlockEntityType.Builder.of(
                    WaystonePortalBlockEntity::new, ModBlocks.WAYSTONE_PORTAL.get()).build(null));

    private ModBlockEntities() {
    }
}
