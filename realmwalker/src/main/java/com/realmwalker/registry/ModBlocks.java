package com.realmwalker.registry;

import java.util.function.Function;

import com.realmwalker.Realmwalker;
import com.realmwalker.block.*;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Realmwalker.MODID);

    public static final DeferredBlock<Block> WAYSTONE_BRICKS = BLOCKS.registerBlock("waystone_bricks", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<WaystonePortalBlock> WAYSTONE_PORTAL = BLOCKS.registerBlock("waystone_portal", WaystonePortalBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noCollission().strength(-1.0F, 3600000.0F).noLootTable().lightLevel(s -> 15).sound(SoundType.GLASS));
    public static final DeferredBlock<ArenaHeartBlock> ARENA_HEART = BLOCKS.registerBlock("arena_heart", ArenaHeartBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(3.0F).sound(SoundType.METAL).lightLevel(s -> 8).noLootTable());
    public static final DeferredBlock<Block> ARENA_HEART_SPENT = BLOCKS.registerBlock("arena_heart_spent", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F).sound(SoundType.STONE).noLootTable());
    public static final DeferredBlock<WaystoneShrineBlock> WAYSTONE_SHRINE = BLOCKS.registerBlock("waystone_shrine", WaystoneShrineBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.0F, 8.0F).sound(SoundType.STONE).lightLevel(s -> 6).requiresCorrectToolForDrops());
    public static final DeferredBlock<GlowcapShrineBlock> GLOWCAP_SHRINE = BLOCKS.registerBlock("glowcap_shrine", GlowcapShrineBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.AMETHYST).lightLevel(s -> 12));
    public static final DeferredBlock<BuriedRuinBlock> BURIED_RUIN = BLOCKS.registerBlock("buried_ruin", BuriedRuinBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(1.5F, 4.0F).sound(SoundType.SAND).requiresCorrectToolForDrops());
    public static final DeferredBlock<GearEngineBlock> GEAR_ENGINE = BLOCKS.registerBlock("gear_engine", GearEngineBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 8.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<BeaconFireSiteBlock> BEACON_FIRE_SITE = BLOCKS.registerBlock("beacon_fire_site", BeaconFireSiteBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.8F).sound(SoundType.SNOW).lightLevel(s -> 4));
    public static final DeferredBlock<MarkedSpireBlock> MARKED_SPIRE = BLOCKS.registerBlock("marked_spire", MarkedSpireBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F).sound(SoundType.METAL).lightLevel(s -> 5).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SPORE_LANTERN = BLOCKS.registerBlock("spore_lantern", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.LANTERN).lightLevel(s -> 14));
    public static final DeferredBlock<Block> AURORA_LANTERN = BLOCKS.registerBlock("aurora_lantern", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.LANTERN).lightLevel(s -> 13));
    public static final DeferredBlock<Block> GLOWCAP = BLOCKS.registerBlock("glowcap", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel(s -> 12));
    public static final DeferredBlock<RotatedPillarBlock> MIREWOOD_LOG = BLOCKS.registerBlock("mirewood_log", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> MIREWOOD_PLANKS = BLOCKS.registerBlock("mirewood_planks", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SPORE_MOSS = BLOCKS.registerBlock("spore_moss", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel(s -> 4));
    public static final DeferredBlock<MurkWaterBlock> MURK_WATER = BLOCKS.registerBlock("murk_water", MurkWaterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().noOcclusion().strength(100.0F).noLootTable().sound(SoundType.GLASS));
    public static final DeferredBlock<Block> SALT_BLOCK = BLOCKS.registerBlock("salt_block", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CRACKED_SALT = BLOCKS.registerBlock("cracked_salt", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<GlassSandBlock> GLASS_SAND = BLOCKS.registerBlock("glass_sand", GlassSandBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.SAND).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> AMBER_CRYSTAL = BLOCKS.registerBlock("amber_crystal", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SUNKEN_BRICK = BLOCKS.registerBlock("sunken_brick", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> BRASS_BLOCK = BLOCKS.registerBlock("brass_block", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GEAR_BLOCK = BLOCKS.registerBlock("gear_block", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<SteamVentBlock> STEAM_VENT = BLOCKS.registerBlock("steam_vent", SteamVentBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(s -> 7));
    public static final DeferredBlock<Block> COG_PLATING = BLOCKS.registerBlock("cog_plating", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> FURNACE_GLASS = BLOCKS.registerBlock("furnace_glass", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> PACKED_FROST = BLOCKS.registerBlock("packed_frost", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.SNOW).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> BONEICE = BLOCKS.registerBlock("boneice", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(0.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> AURORA_GLASS = BLOCKS.registerBlock("aurora_glass", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> FROZEN_MARROW = BLOCKS.registerBlock("frozen_marrow", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<SnowdriftBlock> SNOWDRIFT = BLOCKS.registerBlock("snowdrift", SnowdriftBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).noCollission().instabreak().sound(SoundType.SNOW));
    public static final DeferredBlock<Block> CLOUDSTONE = BLOCKS.registerBlock("cloudstone", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> STORMGLASS = BLOCKS.registerBlock("stormglass", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops());
    public static final DeferredBlock<LightningRodBlock> LIGHTNING_ROD = BLOCKS.registerBlock("lightning_rod", LightningRodBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CHAIN_BLOCK = BLOCKS.registerBlock("chain_block", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> RAIN_MOSS = BLOCKS.registerBlock("rain_moss", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel(s -> 3));
    public static final DeferredBlock<Block> BLOOM_GRASS = BLOCKS.registerBlock("bloom_grass", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.6F).sound(SoundType.GRASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> PETAL_BLOCK = BLOCKS.registerBlock("petal_block", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> SUNPETAL = BLOCKS.registerBlock("sunpetal", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel(s -> 11));

    private static <T extends Block> DeferredBlock<T> register(String name, Function<BlockBehaviour.Properties, T> factory,
            BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, factory, props);
    }

    private ModBlocks() {
    }
}
