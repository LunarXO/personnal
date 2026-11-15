package com.realmwalker.registry;

import com.realmwalker.Realmwalker;
import com.realmwalker.entity.projectile.*;

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

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Realmwalker.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SporeCrawlerEntity>> SPORE_CRAWLER = ENTITIES.register("spore_crawler",
            () -> EntityType.Builder.of(SporeCrawlerEntity::new, MobCategory.MONSTER).sized(1.4F, 0.9F).build("spore_crawler"));
    public static final DeferredHolder<EntityType<?>, EntityType<MirewraithEntity>> MIREWRAITH = ENTITIES.register("mirewraith",
            () -> EntityType.Builder.of(MirewraithEntity::new, MobCategory.MONSTER).sized(0.8F, 1.8F).build("mirewraith"));
    public static final DeferredHolder<EntityType<?>, EntityType<CaplingEntity>> CAPLING = ENTITIES.register("capling",
            () -> EntityType.Builder.of(CaplingEntity::new, MobCategory.MONSTER).sized(0.5F, 0.5F).build("capling"));
    public static final DeferredHolder<EntityType<?>, EntityType<BogLurkerEntity>> BOG_LURKER = ENTITIES.register("bog_lurker",
            () -> EntityType.Builder.of(BogLurkerEntity::new, MobCategory.MONSTER).sized(1.0F, 1.2F).build("bog_lurker"));
    public static final DeferredHolder<EntityType<?>, EntityType<GlowMothEntity>> GLOW_MOTH = ENTITIES.register("glow_moth",
            () -> EntityType.Builder.of(GlowMothEntity::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build("glow_moth"));
    public static final DeferredHolder<EntityType<?>, EntityType<MirefatherEntity>> MIREFATHER = ENTITIES.register("mirefather",
            () -> EntityType.Builder.of(MirefatherEntity::new, MobCategory.MONSTER).sized(1.6F, 3.2F).build("mirefather"));
    public static final DeferredHolder<EntityType<?>, EntityType<GlassScorpionEntity>> GLASS_SCORPION = ENTITIES.register("glass_scorpion",
            () -> EntityType.Builder.of(GlassScorpionEntity::new, MobCategory.MONSTER).sized(1.2F, 0.8F).build("glass_scorpion"));
    public static final DeferredHolder<EntityType<?>, EntityType<MiragePhantomEntity>> MIRAGE_PHANTOM = ENTITIES.register("mirage_phantom",
            () -> EntityType.Builder.of(MiragePhantomEntity::new, MobCategory.MONSTER).sized(0.7F, 1.9F).build("mirage_phantom"));
    public static final DeferredHolder<EntityType<?>, EntityType<SaltHuskEntity>> SALT_HUSK = ENTITIES.register("salt_husk",
            () -> EntityType.Builder.of(SaltHuskEntity::new, MobCategory.MONSTER).sized(0.9F, 2.0F).build("salt_husk"));
    public static final DeferredHolder<EntityType<?>, EntityType<DuneStriderEntity>> DUNE_STRIDER = ENTITIES.register("dune_strider",
            () -> EntityType.Builder.of(DuneStriderEntity::new, MobCategory.MONSTER).sized(1.0F, 1.6F).build("dune_strider"));
    public static final DeferredHolder<EntityType<?>, EntityType<BasinEelEntity>> BASIN_EEL = ENTITIES.register("basin_eel",
            () -> EntityType.Builder.of(BasinEelEntity::new, MobCategory.MONSTER).sized(0.8F, 0.4F).build("basin_eel"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrismColossusEntity>> PRISM_COLOSSUS = ENTITIES.register("prism_colossus",
            () -> EntityType.Builder.of(PrismColossusEntity::new, MobCategory.MONSTER).sized(1.6F, 3.2F).build("prism_colossus"));
    public static final DeferredHolder<EntityType<?>, EntityType<ClockworkSoldierEntity>> CLOCKWORK_SOLDIER = ENTITIES.register("clockwork_soldier",
            () -> EntityType.Builder.of(ClockworkSoldierEntity::new, MobCategory.MONSTER).sized(0.7F, 1.9F).build("clockwork_soldier"));
    public static final DeferredHolder<EntityType<?>, EntityType<RivetDroneEntity>> RIVET_DRONE = ENTITIES.register("rivet_drone",
            () -> EntityType.Builder.of(RivetDroneEntity::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build("rivet_drone"));
    public static final DeferredHolder<EntityType<?>, EntityType<SteamGolemEntity>> STEAM_GOLEM = ENTITIES.register("steam_golem",
            () -> EntityType.Builder.of(SteamGolemEntity::new, MobCategory.MONSTER).sized(1.4F, 2.6F).build("steam_golem"));
    public static final DeferredHolder<EntityType<?>, EntityType<ScrapHoundEntity>> SCRAP_HOUND = ENTITIES.register("scrap_hound",
            () -> EntityType.Builder.of(ScrapHoundEntity::new, MobCategory.MONSTER).sized(0.8F, 0.9F).build("scrap_hound"));
    public static final DeferredHolder<EntityType<?>, EntityType<GearwrightEntity>> GEARWRIGHT = ENTITIES.register("gearwright",
            () -> EntityType.Builder.of(GearwrightEntity::new, MobCategory.MONSTER).sized(0.7F, 1.9F).build("gearwright"));
    public static final DeferredHolder<EntityType<?>, EntityType<GrandMechanistEntity>> GRAND_MECHANIST = ENTITIES.register("grand_mechanist",
            () -> EntityType.Builder.of(GrandMechanistEntity::new, MobCategory.MONSTER).sized(1.6F, 3.2F).build("grand_mechanist"));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostboundEntity>> FROSTBOUND = ENTITIES.register("frostbound",
            () -> EntityType.Builder.of(FrostboundEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).build("frostbound"));
    public static final DeferredHolder<EntityType<?>, EntityType<RimeWolfEntity>> RIME_WOLF = ENTITIES.register("rime_wolf",
            () -> EntityType.Builder.of(RimeWolfEntity::new, MobCategory.MONSTER).sized(0.8F, 0.9F).build("rime_wolf"));
    public static final DeferredHolder<EntityType<?>, EntityType<MarrowWraithEntity>> MARROW_WRAITH = ENTITIES.register("marrow_wraith",
            () -> EntityType.Builder.of(MarrowWraithEntity::new, MobCategory.MONSTER).sized(0.7F, 1.9F).build("marrow_wraith"));
    public static final DeferredHolder<EntityType<?>, EntityType<IcicleSpitterEntity>> ICICLE_SPITTER = ENTITIES.register("icicle_spitter",
            () -> EntityType.Builder.of(IcicleSpitterEntity::new, MobCategory.MONSTER).sized(0.7F, 1.4F).build("icicle_spitter"));
    public static final DeferredHolder<EntityType<?>, EntityType<AuroraSpriteEntity>> AURORA_SPRITE = ENTITIES.register("aurora_sprite",
            () -> EntityType.Builder.of(AuroraSpriteEntity::new, MobCategory.MONSTER).sized(0.5F, 0.9F).build("aurora_sprite"));
    public static final DeferredHolder<EntityType<?>, EntityType<RibcageWyrmEntity>> RIBCAGE_WYRM = ENTITIES.register("ribcage_wyrm",
            () -> EntityType.Builder.of(RibcageWyrmEntity::new, MobCategory.MONSTER).sized(1.6F, 3.2F).build("ribcage_wyrm"));
    public static final DeferredHolder<EntityType<?>, EntityType<StormHawkEntity>> STORM_HAWK = ENTITIES.register("storm_hawk",
            () -> EntityType.Builder.of(StormHawkEntity::new, MobCategory.MONSTER).sized(0.9F, 0.7F).build("storm_hawk"));
    public static final DeferredHolder<EntityType<?>, EntityType<ThunderImpEntity>> THUNDER_IMP = ENTITIES.register("thunder_imp",
            () -> EntityType.Builder.of(ThunderImpEntity::new, MobCategory.MONSTER).sized(0.5F, 0.9F).build("thunder_imp"));
    public static final DeferredHolder<EntityType<?>, EntityType<GaleElementalEntity>> GALE_ELEMENTAL = ENTITIES.register("gale_elemental",
            () -> EntityType.Builder.of(GaleElementalEntity::new, MobCategory.MONSTER).sized(1.0F, 1.8F).build("gale_elemental"));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudJellyEntity>> CLOUD_JELLY = ENTITIES.register("cloud_jelly",
            () -> EntityType.Builder.of(CloudJellyEntity::new, MobCategory.MONSTER).sized(0.9F, 0.9F).build("cloud_jelly"));
    public static final DeferredHolder<EntityType<?>, EntityType<RodKnightEntity>> ROD_KNIGHT = ENTITIES.register("rod_knight",
            () -> EntityType.Builder.of(RodKnightEntity::new, MobCategory.MONSTER).sized(0.8F, 2.0F).build("rod_knight"));
    public static final DeferredHolder<EntityType<?>, EntityType<TempestSovereignEntity>> TEMPEST_SOVEREIGN = ENTITIES.register("tempest_sovereign",
            () -> EntityType.Builder.of(TempestSovereignEntity::new, MobCategory.MONSTER).sized(1.6F, 3.2F).build("tempest_sovereign"));

    public static final DeferredHolder<EntityType<?>, EntityType<RealmwalkerProjectile>> MOB_BOLT = ENTITIES.register("mob_bolt",
            () -> EntityType.Builder.<RealmwalkerProjectile>of(RealmwalkerProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("mob_bolt"));
    public static final DeferredHolder<EntityType<?>, EntityType<HookEntity>> HOOK = ENTITIES.register("hook",
            () -> EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("hook"));
    public static final DeferredHolder<EntityType<?>, EntityType<BoneSpearEntity>> BONE_SPEAR = ENTITIES.register("bone_spear_projectile",
            () -> EntityType.Builder.<BoneSpearEntity>of(BoneSpearEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("bone_spear_projectile"));

    private ModEntities() {
    }
}
