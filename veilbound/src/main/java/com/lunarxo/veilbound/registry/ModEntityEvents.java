package com.lunarxo.veilbound.registry;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.entity.CrystalGolemEntity;
import com.lunarxo.veilbound.entity.EmberWispEntity;
import com.lunarxo.veilbound.entity.GlimmerfoxEntity;
import com.lunarxo.veilbound.entity.HollowHuskEntity;
import com.lunarxo.veilbound.entity.HollowKingEntity;
import com.lunarxo.veilbound.entity.ShardlingEntity;
import com.lunarxo.veilbound.entity.VeilMothEntity;
import com.lunarxo.veilbound.entity.VeilReaverEntity;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = Veilbound.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GLIMMERFOX.get(), GlimmerfoxEntity.createAttributes().build());
        event.put(ModEntities.SHARDLING.get(), ShardlingEntity.createAttributes().build());
        event.put(ModEntities.VEIL_MOTH.get(), VeilMothEntity.createAttributes().build());
        event.put(ModEntities.HOLLOW_HUSK.get(), HollowHuskEntity.createAttributes().build());
        event.put(ModEntities.VEIL_REAVER.get(), VeilReaverEntity.createAttributes().build());
        event.put(ModEntities.EMBER_WISP.get(), EmberWispEntity.createAttributes().build());
        event.put(ModEntities.CRYSTAL_GOLEM.get(), CrystalGolemEntity.createAttributes().build());
        event.put(ModEntities.HOLLOW_KING.get(), HollowKingEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.GLIMMERFOX.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlimmerfoxEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.SHARDLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ShardlingEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.VEIL_MOTH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VeilMothEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.HOLLOW_HUSK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HollowHuskEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.VEIL_REAVER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VeilReaverEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.EMBER_WISP.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                EmberWispEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CRYSTAL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CrystalGolemEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.HOLLOW_KING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HollowKingEntity::checkVeilSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
