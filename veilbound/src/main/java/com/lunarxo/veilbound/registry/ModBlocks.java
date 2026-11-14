package com.lunarxo.veilbound.registry;

import java.util.function.Function;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.block.EmberBrazierBlock;
import com.lunarxo.veilbound.block.GravityAnchorBlock;
import com.lunarxo.veilbound.block.ResonantSpireBlock;
import com.lunarxo.veilbound.block.VeilPlantBlock;
import com.lunarxo.veilbound.block.VeilPortalBlock;
import com.lunarxo.veilbound.block.VeilPortalFrameBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Veilbound.MODID);

    private static final BlockBehaviour.Properties VEILSTONE_PROPS = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE).strength(1.5F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();

    public static final DeferredBlock<Block> VEILSTONE = register("veilstone", Block::new, VEILSTONE_PROPS);
    public static final DeferredBlock<Block> COBBLED_VEILSTONE = register("cobbled_veilstone", Block::new, VEILSTONE_PROPS);
    public static final DeferredBlock<Block> VEILSTONE_BRICKS = register("veilstone_bricks", Block::new, VEILSTONE_PROPS);
    public static final DeferredBlock<Block> CHISELED_VEILSTONE = register("chiseled_veilstone", Block::new, VEILSTONE_PROPS);
    public static final DeferredBlock<StairBlock> VEILSTONE_STAIRS = register("veilstone_stairs",
            p -> new StairBlock(VEILSTONE.get().defaultBlockState(), p), VEILSTONE_PROPS);
    public static final DeferredBlock<SlabBlock> VEILSTONE_SLAB = register("veilstone_slab", SlabBlock::new, VEILSTONE_PROPS);
    public static final DeferredBlock<WallBlock> VEILSTONE_WALL = register("veilstone_wall", WallBlock::new, VEILSTONE_PROPS);

    public static final DeferredBlock<Block> VEIL_TURF = register("veil_turf", Block::new, VEILSTONE_PROPS);

    public static final DeferredBlock<Block> VOIDSTONE = register("voidstone", Block::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F, 8.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> VOIDSTONE_BRICKS = register("voidstone_bricks", Block::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F, 8.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());

    private static final BlockBehaviour.Properties ORE_PROPS = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE).strength(3.0F, 3.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops();

    public static final DeferredBlock<Block> AETHERITE_ORE = register("aetherite_ore", Block::new, ORE_PROPS);
    public static final DeferredBlock<Block> DEEPSLATE_AETHERITE_ORE = register("deepslate_aetherite_ore", Block::new, ORE_PROPS);
    public static final DeferredBlock<Block> VOIDSTONE_ORE = register("voidstone_ore", Block::new, ORE_PROPS);
    public static final DeferredBlock<Block> GLIMMER_SHARD_ORE = register("glimmer_shard_ore", Block::new, ORE_PROPS);

    public static final DeferredBlock<Block> GLIMMER_CRYSTAL_BLOCK = register("glimmer_crystal_block", Block::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.8F).sound(SoundType.AMETHYST)
                    .lightLevel(s -> 11).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> RAW_AETHERITE_BLOCK = register("raw_aetherite_block", Block::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> RAW_VOIDSTONE_BLOCK = register("raw_voidstone_block", Block::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());

    public static final DeferredBlock<VeilPortalFrameBlock> VEIL_PORTAL_FRAME = register("veil_portal_frame",
            VeilPortalFrameBlock::new, VEILSTONE_PROPS);
    public static final DeferredBlock<VeilPortalBlock> VEIL_PORTAL = BLOCKS.registerBlock("veil_portal",
            VeilPortalBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission()
                    .strength(-1.0F, 3600000.0F).noLootTable().lightLevel(s -> 15).sound(SoundType.GLASS));

    public static final DeferredBlock<GravityAnchorBlock> GRAVITY_ANCHOR = register("gravity_anchor",
            GravityAnchorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(3.5F, 9.0F)
                    .sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(s -> 7));
    public static final DeferredBlock<ResonantSpireBlock> RESONANT_SPIRE = register("resonant_spire",
            ResonantSpireBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.5F, 6.0F)
                    .sound(SoundType.AMETHYST).requiresCorrectToolForDrops().lightLevel(s -> 10));
    public static final DeferredBlock<EmberBrazierBlock> EMBER_BRAZIER = register("ember_brazier",
            EmberBrazierBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.0F)
                    .sound(SoundType.METAL).lightLevel(s -> 15).requiresCorrectToolForDrops());

    private static final BlockBehaviour.Properties PLANT_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
            .noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ);

    public static final DeferredBlock<VeilPlantBlock> VEIL_GRASS = register("veil_grass", VeilPlantBlock::new, PLANT_PROPS);
    public static final DeferredBlock<VeilPlantBlock> VEIL_BLOOM = register("veil_bloom", VeilPlantBlock::new, PLANT_PROPS);
    public static final DeferredBlock<VeilPlantBlock> HANGING_VEIL_VINES = register("hanging_veil_vines", VeilPlantBlock::new, PLANT_PROPS);
    public static final DeferredBlock<VeilPlantBlock> GLOWING_VEIL_FUNGUS = register("glowing_veil_fungus", VeilPlantBlock::new,
            PLANT_PROPS.lightLevel(s -> 9));

    private static <T extends Block> DeferredBlock<T> register(String name, Function<BlockBehaviour.Properties, T> factory,
            BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, factory, props);
    }

}
