package com.realmwalker.registry;

import com.realmwalker.Realmwalker;

import com.realmwalker.entity.lumenfen.SporeCrawlerEntity;
import com.realmwalker.entity.lumenfen.MirewraithEntity;
import com.realmwalker.entity.lumenfen.CaplingEntity;
import com.realmwalker.entity.lumenfen.BogLurkerEntity;
import com.realmwalker.entity.lumenfen.GlowMothEntity;
import com.realmwalker.entity.lumenfen.MirefatherEntity;
import com.realmwalker.entity.saltglass_expanse.GlassScorpionEntity;
import com.realmwalker.entity.saltglass_expanse.MiragePhantomEntity;
import com.realmwalker.entity.saltglass_expanse.SaltHuskEntity;
import com.realmwalker.entity.saltglass_expanse.DuneStriderEntity;
import com.realmwalker.entity.saltglass_expanse.BasinEelEntity;
import com.realmwalker.entity.saltglass_expanse.PrismColossusEntity;
import com.realmwalker.entity.cogspire.ClockworkSoldierEntity;
import com.realmwalker.entity.cogspire.RivetDroneEntity;
import com.realmwalker.entity.cogspire.SteamGolemEntity;
import com.realmwalker.entity.cogspire.ScrapHoundEntity;
import com.realmwalker.entity.cogspire.GearwrightEntity;
import com.realmwalker.entity.cogspire.GrandMechanistEntity;
import com.realmwalker.entity.frostmarrow.FrostboundEntity;
import com.realmwalker.entity.frostmarrow.RimeWolfEntity;
import com.realmwalker.entity.frostmarrow.MarrowWraithEntity;
import com.realmwalker.entity.frostmarrow.IcicleSpitterEntity;
import com.realmwalker.entity.frostmarrow.AuroraSpriteEntity;
import com.realmwalker.entity.frostmarrow.RibcageWyrmEntity;
import com.realmwalker.entity.stormspire.StormHawkEntity;
import com.realmwalker.entity.stormspire.ThunderImpEntity;
import com.realmwalker.entity.stormspire.GaleElementalEntity;
import com.realmwalker.entity.stormspire.CloudJellyEntity;
import com.realmwalker.entity.stormspire.RodKnightEntity;
import com.realmwalker.entity.stormspire.TempestSovereignEntity;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = Realmwalker.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(ModEntities.SPORE_CRAWLER.get(), SporeCrawlerEntity.createAttributes().build());
        event.put(ModEntities.MIREWRAITH.get(), MirewraithEntity.createAttributes().build());
        event.put(ModEntities.CAPLING.get(), CaplingEntity.createAttributes().build());
        event.put(ModEntities.BOG_LURKER.get(), BogLurkerEntity.createAttributes().build());
        event.put(ModEntities.GLOW_MOTH.get(), GlowMothEntity.createAttributes().build());
        event.put(ModEntities.MIREFATHER.get(), MirefatherEntity.createAttributes().build());
        event.put(ModEntities.GLASS_SCORPION.get(), GlassScorpionEntity.createAttributes().build());
        event.put(ModEntities.MIRAGE_PHANTOM.get(), MiragePhantomEntity.createAttributes().build());
        event.put(ModEntities.SALT_HUSK.get(), SaltHuskEntity.createAttributes().build());
        event.put(ModEntities.DUNE_STRIDER.get(), DuneStriderEntity.createAttributes().build());
        event.put(ModEntities.BASIN_EEL.get(), BasinEelEntity.createAttributes().build());
        event.put(ModEntities.PRISM_COLOSSUS.get(), PrismColossusEntity.createAttributes().build());
        event.put(ModEntities.CLOCKWORK_SOLDIER.get(), ClockworkSoldierEntity.createAttributes().build());
        event.put(ModEntities.RIVET_DRONE.get(), RivetDroneEntity.createAttributes().build());
        event.put(ModEntities.STEAM_GOLEM.get(), SteamGolemEntity.createAttributes().build());
        event.put(ModEntities.SCRAP_HOUND.get(), ScrapHoundEntity.createAttributes().build());
        event.put(ModEntities.GEARWRIGHT.get(), GearwrightEntity.createAttributes().build());
        event.put(ModEntities.GRAND_MECHANIST.get(), GrandMechanistEntity.createAttributes().build());
        event.put(ModEntities.FROSTBOUND.get(), FrostboundEntity.createAttributes().build());
        event.put(ModEntities.RIME_WOLF.get(), RimeWolfEntity.createAttributes().build());
        event.put(ModEntities.MARROW_WRAITH.get(), MarrowWraithEntity.createAttributes().build());
        event.put(ModEntities.ICICLE_SPITTER.get(), IcicleSpitterEntity.createAttributes().build());
        event.put(ModEntities.AURORA_SPRITE.get(), AuroraSpriteEntity.createAttributes().build());
        event.put(ModEntities.RIBCAGE_WYRM.get(), RibcageWyrmEntity.createAttributes().build());
        event.put(ModEntities.STORM_HAWK.get(), StormHawkEntity.createAttributes().build());
        event.put(ModEntities.THUNDER_IMP.get(), ThunderImpEntity.createAttributes().build());
        event.put(ModEntities.GALE_ELEMENTAL.get(), GaleElementalEntity.createAttributes().build());
        event.put(ModEntities.CLOUD_JELLY.get(), CloudJellyEntity.createAttributes().build());
        event.put(ModEntities.ROD_KNIGHT.get(), RodKnightEntity.createAttributes().build());
        event.put(ModEntities.TEMPEST_SOVEREIGN.get(), TempestSovereignEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {

        event.register(ModEntities.SPORE_CRAWLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SporeCrawlerEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MIREWRAITH.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MirewraithEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CAPLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CaplingEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.BOG_LURKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BogLurkerEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GLOW_MOTH.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowMothEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GLASS_SCORPION.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlassScorpionEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MIRAGE_PHANTOM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MiragePhantomEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.SALT_HUSK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SaltHuskEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.DUNE_STRIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                DuneStriderEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.BASIN_EEL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BasinEelEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CLOCKWORK_SOLDIER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ClockworkSoldierEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.RIVET_DRONE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RivetDroneEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.STEAM_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SteamGolemEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.SCRAP_HOUND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ScrapHoundEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GEARWRIGHT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GearwrightEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.FROSTBOUND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FrostboundEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.RIME_WOLF.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RimeWolfEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MARROW_WRAITH.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MarrowWraithEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.ICICLE_SPITTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                IcicleSpitterEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.AURORA_SPRITE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AuroraSpriteEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.STORM_HAWK.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                StormHawkEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.THUNDER_IMP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ThunderImpEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.GALE_ELEMENTAL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GaleElementalEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CLOUD_JELLY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CloudJellyEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.ROD_KNIGHT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RodKnightEntity::checkSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
