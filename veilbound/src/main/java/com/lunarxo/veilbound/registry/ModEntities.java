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
import com.lunarxo.veilbound.entity.projectile.EmberOrbEntity;
import com.lunarxo.veilbound.entity.projectile.GloomOrbEntity;
import com.lunarxo.veilbound.entity.projectile.FireballEntity;
import com.lunarxo.veilbound.entity.projectile.ShardBoltEntity;
import com.lunarxo.veilbound.entity.projectile.StormBoltEntity;
import com.lunarxo.veilbound.entity.projectile.VeilArrowEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Veilbound.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<GlimmerfoxEntity>> GLIMMERFOX = ENTITIES.register("glimmerfox",
            () -> EntityType.Builder.of(GlimmerfoxEntity::new, MobCategory.CREATURE).sized(0.6F, 0.5F).build("glimmerfox"));
    public static final DeferredHolder<EntityType<?>, EntityType<ShardlingEntity>> SHARDLING = ENTITIES.register("shardling",
            () -> EntityType.Builder.of(ShardlingEntity::new, MobCategory.CREATURE).sized(0.4F, 0.3F).build("shardling"));
    public static final DeferredHolder<EntityType<?>, EntityType<VeilMothEntity>> VEIL_MOTH = ENTITIES.register("veil_moth",
            () -> EntityType.Builder.of(VeilMothEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).build("veil_moth"));

    public static final DeferredHolder<EntityType<?>, EntityType<HollowHuskEntity>> HOLLOW_HUSK = ENTITIES.register("hollow_husk",
            () -> EntityType.Builder.of(HollowHuskEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build("hollow_husk"));
    public static final DeferredHolder<EntityType<?>, EntityType<VeilReaverEntity>> VEIL_REAVER = ENTITIES.register("veil_reaver",
            () -> EntityType.Builder.of(VeilReaverEntity::new, MobCategory.MONSTER).sized(0.8F, 2.2F).build("veil_reaver"));
    public static final DeferredHolder<EntityType<?>, EntityType<EmberWispEntity>> EMBER_WISP = ENTITIES.register("ember_wisp",
            () -> EntityType.Builder.of(EmberWispEntity::new, MobCategory.MONSTER).sized(0.6F, 0.6F).build("ember_wisp"));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalGolemEntity>> CRYSTAL_GOLEM = ENTITIES.register("crystal_golem",
            () -> EntityType.Builder.of(CrystalGolemEntity::new, MobCategory.MONSTER).sized(1.4F, 2.7F).build("crystal_golem"));
    public static final DeferredHolder<EntityType<?>, EntityType<HollowKingEntity>> HOLLOW_KING = ENTITIES.register("hollow_king",
            () -> EntityType.Builder.of(HollowKingEntity::new, MobCategory.MONSTER).sized(1.0F, 2.8F).build("hollow_king"));

    public static final DeferredHolder<EntityType<?>, EntityType<EmberOrbEntity>> EMBER_ORB = ENTITIES.register("ember_orb",
            () -> EntityType.Builder.<EmberOrbEntity>of(EmberOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("ember_orb"));
    public static final DeferredHolder<EntityType<?>, EntityType<ShardBoltEntity>> SHARD_BOLT = ENTITIES.register("shard_bolt",
            () -> EntityType.Builder.<ShardBoltEntity>of(ShardBoltEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("shard_bolt"));
    public static final DeferredHolder<EntityType<?>, EntityType<GloomOrbEntity>> GLOOM_ORB = ENTITIES.register("gloom_orb",
            () -> EntityType.Builder.<GloomOrbEntity>of(GloomOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("gloom_orb"));
    public static final DeferredHolder<EntityType<?>, EntityType<StormBoltEntity>> STORM_BOLT = ENTITIES.register("storm_bolt",
            () -> EntityType.Builder.<StormBoltEntity>of(StormBoltEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("storm_bolt"));
    public static final DeferredHolder<EntityType<?>, EntityType<FireballEntity>> FIREBALL = ENTITIES.register("fireball",
            () -> EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("fireball"));
    public static final DeferredHolder<EntityType<?>, EntityType<VeilArrowEntity>> VEIL_ARROW = ENTITIES.register("veil_arrow",
            () -> EntityType.Builder.<VeilArrowEntity>of(VeilArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("veil_arrow"));
}
