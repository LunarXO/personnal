package com.lunarxo.veilbound.registry;

import java.util.function.Function;

import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.item.VeilArrowItem;
import com.lunarxo.veilbound.item.VeilKeyItem;
import com.lunarxo.veilbound.item.trinket.AmuletOfSlowFallItem;
import com.lunarxo.veilbound.item.trinket.GravityRingItem;
import com.lunarxo.veilbound.item.trinket.VeilCloakItem;
import com.lunarxo.veilbound.item.weapon.AetherfangDaggerItem;
import com.lunarxo.veilbound.item.weapon.EmberbrandSwordItem;
import com.lunarxo.veilbound.item.weapon.GloomWandItem;
import com.lunarxo.veilbound.item.weapon.EmberStaffItem;
import com.lunarxo.veilbound.item.weapon.ShardLauncherItem;
import com.lunarxo.veilbound.item.weapon.StormScepterItem;
import com.lunarxo.veilbound.item.weapon.StormcallerGreatswordItem;
import com.lunarxo.veilbound.item.weapon.VoidreaverScytheItem;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Veilbound.MODID);

    // Materials
    public static final DeferredItem<Item> RAW_AETHERITE = simple("raw_aetherite");
    public static final DeferredItem<Item> AETHERITE_INGOT = simple("aetherite_ingot");
    public static final DeferredItem<Item> RAW_VOIDSTONE = simple("raw_voidstone");
    public static final DeferredItem<Item> VOIDSTONE_INGOT = simple("voidstone_ingot");
    public static final DeferredItem<Item> GLIMMER_SHARD = simple("glimmer_shard");
    public static final DeferredItem<Item> EMBER_CORE = simple("ember_core");
    public static final DeferredItem<Item> VOID_HEART = simple("void_heart");
    public static final DeferredItem<Item> VEIL_ESSENCE = simple("veil_essence");

    // Foods
    public static final DeferredItem<Item> VEILBERRY = ITEMS.registerItem("veilberry", p -> new Item(p.food(
            new FoodProperties.Builder().nutrition(3).saturationModifier(0.4F).build())));
    public static final DeferredItem<Item> EMBER_FRUIT = ITEMS.registerItem("ember_fruit", p -> new Item(p.food(
            new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F)
                    .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0), 1.0F).build())));
    public static final DeferredItem<Item> GLOOM_STEW = ITEMS.registerItem("gloom_stew", p -> new Item(p.stacksTo(1).food(
            new FoodProperties.Builder().nutrition(6).saturationModifier(0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0), 1.0F)
                    .usingConvertsTo(Items.BOWL).build())));

    // Tools and weapons, per tier
    public static final DeferredItem<EmberbrandSwordItem> EMBERBRAND_SWORD = ITEMS.registerItem("emberbrand_sword",
            p -> new EmberbrandSwordItem(ModTiers.EMBER, p.durability(ModTiers.EMBER.getUses())));
    public static final DeferredItem<PickaxeItem> EMBER_PICKAXE = tool("ember_pickaxe", ModTiers.EMBER,
            PickaxeItem::new);
    public static final DeferredItem<AxeItem> EMBER_AXE = tool("ember_axe", ModTiers.EMBER,
            AxeItem::new);
    public static final DeferredItem<ShovelItem> EMBER_SHOVEL = tool("ember_shovel", ModTiers.EMBER,
            ShovelItem::new);
    public static final DeferredItem<HoeItem> EMBER_HOE = tool("ember_hoe", ModTiers.EMBER,
            HoeItem::new);

    public static final DeferredItem<StormcallerGreatswordItem> STORMCALLER_GREATSWORD = ITEMS.registerItem("stormcaller_greatsword",
            p -> new StormcallerGreatswordItem(ModTiers.AETHERITE, p.durability(ModTiers.AETHERITE.getUses())));
    public static final DeferredItem<PickaxeItem> AETHERITE_PICKAXE = tool("aetherite_pickaxe", ModTiers.AETHERITE,
            PickaxeItem::new);
    public static final DeferredItem<AxeItem> AETHERITE_AXE = tool("aetherite_axe", ModTiers.AETHERITE,
            AxeItem::new);
    public static final DeferredItem<ShovelItem> AETHERITE_SHOVEL = tool("aetherite_shovel", ModTiers.AETHERITE,
            ShovelItem::new);
    public static final DeferredItem<HoeItem> AETHERITE_HOE = tool("aetherite_hoe", ModTiers.AETHERITE,
            HoeItem::new);

    public static final DeferredItem<VoidreaverScytheItem> VOIDREAVER_SCYTHE = ITEMS.registerItem("voidreaver_scythe",
            p -> new VoidreaverScytheItem(ModTiers.VOIDSTEEL, p.durability(ModTiers.VOIDSTEEL.getUses())));
    public static final DeferredItem<PickaxeItem> VOIDSTEEL_PICKAXE = tool("voidsteel_pickaxe", ModTiers.VOIDSTEEL,
            PickaxeItem::new);
    public static final DeferredItem<AxeItem> VOIDSTEEL_AXE = tool("voidsteel_axe", ModTiers.VOIDSTEEL,
            AxeItem::new);
    public static final DeferredItem<ShovelItem> VOIDSTEEL_SHOVEL = tool("voidsteel_shovel", ModTiers.VOIDSTEEL,
            ShovelItem::new);
    public static final DeferredItem<HoeItem> VOIDSTEEL_HOE = tool("voidsteel_hoe", ModTiers.VOIDSTEEL,
            HoeItem::new);

    public static final DeferredItem<AetherfangDaggerItem> AETHERFANG_DAGGER = ITEMS.registerItem("aetherfang_dagger",
            p -> new AetherfangDaggerItem());

    // Ranged
    public static final DeferredItem<BowItem> VEILBOW = ITEMS.registerItem("veilbow",
            p -> new BowItem(p.durability(640)));
    public static final DeferredItem<VeilArrowItem> VEIL_ARROW = ITEMS.registerItem("veil_arrow",
            VeilArrowItem::new);
    public static final DeferredItem<ShardLauncherItem> SHARD_LAUNCHER = ITEMS.registerItem("shard_launcher",
            ShardLauncherItem::new);
    public static final DeferredItem<GloomWandItem> GLOOM_WAND = ITEMS.registerItem("gloom_wand", GloomWandItem::new);
    public static final DeferredItem<StormScepterItem> STORM_SCEPTER = ITEMS.registerItem("storm_scepter", StormScepterItem::new);
    public static final DeferredItem<EmberStaffItem> EMBER_STAFF = ITEMS.registerItem("ember_staff", EmberStaffItem::new);

    // Trinkets and utility
    public static final DeferredItem<VeilKeyItem> VEIL_KEY = ITEMS.registerItem("veil_key",
            p -> new VeilKeyItem(p, ModBlocks.VEIL_PORTAL_FRAME::get, ModBlocks.VEIL_PORTAL::get));
    public static final DeferredItem<VeilKeyItem> ABYSS_KEY = ITEMS.registerItem("abyss_key",
            p -> new VeilKeyItem(p, ModBlocks.ABYSS_PORTAL_FRAME::get, ModBlocks.ABYSS_PORTAL::get));
    public static final DeferredItem<AmuletOfSlowFallItem> AMULET_OF_SLOW_FALL = ITEMS.registerItem("amulet_of_slow_fall",
            AmuletOfSlowFallItem::new);
    public static final DeferredItem<GravityRingItem> GRAVITY_RING = ITEMS.registerItem("gravity_ring", GravityRingItem::new);
    public static final DeferredItem<VeilCloakItem> VEIL_CLOAK = ITEMS.registerItem("veil_cloak", VeilCloakItem::new);

    // Armor sets
    public static final DeferredItem<ArmorItem> EMBER_HELMET = armor("ember_helmet", ModArmorMaterials.EMBER, ArmorItem.Type.HELMET, ModArmorMaterials.EMBER_DURABILITY);
    public static final DeferredItem<ArmorItem> EMBER_CHESTPLATE = armor("ember_chestplate", ModArmorMaterials.EMBER, ArmorItem.Type.CHESTPLATE, ModArmorMaterials.EMBER_DURABILITY);
    public static final DeferredItem<ArmorItem> EMBER_LEGGINGS = armor("ember_leggings", ModArmorMaterials.EMBER, ArmorItem.Type.LEGGINGS, ModArmorMaterials.EMBER_DURABILITY);
    public static final DeferredItem<ArmorItem> EMBER_BOOTS = armor("ember_boots", ModArmorMaterials.EMBER, ArmorItem.Type.BOOTS, ModArmorMaterials.EMBER_DURABILITY);
    public static final DeferredItem<ArmorItem> AETHERITE_HELMET = armor("aetherite_helmet", ModArmorMaterials.AETHERITE, ArmorItem.Type.HELMET, ModArmorMaterials.AETHERITE_DURABILITY);
    public static final DeferredItem<ArmorItem> AETHERITE_CHESTPLATE = armor("aetherite_chestplate", ModArmorMaterials.AETHERITE, ArmorItem.Type.CHESTPLATE, ModArmorMaterials.AETHERITE_DURABILITY);
    public static final DeferredItem<ArmorItem> AETHERITE_LEGGINGS = armor("aetherite_leggings", ModArmorMaterials.AETHERITE, ArmorItem.Type.LEGGINGS, ModArmorMaterials.AETHERITE_DURABILITY);
    public static final DeferredItem<ArmorItem> AETHERITE_BOOTS = armor("aetherite_boots", ModArmorMaterials.AETHERITE, ArmorItem.Type.BOOTS, ModArmorMaterials.AETHERITE_DURABILITY);
    public static final DeferredItem<ArmorItem> VOIDSTEEL_HELMET = armor("voidsteel_helmet", ModArmorMaterials.VOIDSTEEL, ArmorItem.Type.HELMET, ModArmorMaterials.VOIDSTEEL_DURABILITY);
    public static final DeferredItem<ArmorItem> VOIDSTEEL_CHESTPLATE = armor("voidsteel_chestplate", ModArmorMaterials.VOIDSTEEL, ArmorItem.Type.CHESTPLATE, ModArmorMaterials.VOIDSTEEL_DURABILITY);
    public static final DeferredItem<ArmorItem> VOIDSTEEL_LEGGINGS = armor("voidsteel_leggings", ModArmorMaterials.VOIDSTEEL, ArmorItem.Type.LEGGINGS, ModArmorMaterials.VOIDSTEEL_DURABILITY);
    public static final DeferredItem<ArmorItem> VOIDSTEEL_BOOTS = armor("voidsteel_boots", ModArmorMaterials.VOIDSTEEL, ArmorItem.Type.BOOTS, ModArmorMaterials.VOIDSTEEL_DURABILITY);

    // Spawn eggs
    public static final DeferredItem<DeferredSpawnEggItem> GLIMMERFOX_SPAWN_EGG = egg("glimmerfox_spawn_egg", ModEntities.GLIMMERFOX::get, 0xB06AFF, 0xE8D8FF);
    public static final DeferredItem<DeferredSpawnEggItem> SHARDLING_SPAWN_EGG = egg("shardling_spawn_egg", ModEntities.SHARDLING::get, 0x7FE0D0, 0x2E8B7A);
    public static final DeferredItem<DeferredSpawnEggItem> VEIL_MOTH_SPAWN_EGG = egg("veil_moth_spawn_egg", ModEntities.VEIL_MOTH::get, 0x4A3A6B, 0x9B8BD0);
    public static final DeferredItem<DeferredSpawnEggItem> HOLLOW_HUSK_SPAWN_EGG = egg("hollow_husk_spawn_egg", ModEntities.HOLLOW_HUSK::get, 0x2B2140, 0x6B4FA0);
    public static final DeferredItem<DeferredSpawnEggItem> VEIL_REAVER_SPAWN_EGG = egg("veil_reaver_spawn_egg", ModEntities.VEIL_REAVER::get, 0x3A1F5D, 0xC04040);
    public static final DeferredItem<DeferredSpawnEggItem> EMBER_WISP_SPAWN_EGG = egg("ember_wisp_spawn_egg", ModEntities.EMBER_WISP::get, 0xFF7722, 0xFFD080);
    public static final DeferredItem<DeferredSpawnEggItem> CRYSTAL_GOLEM_SPAWN_EGG = egg("crystal_golem_spawn_egg", ModEntities.CRYSTAL_GOLEM::get, 0x8FE8FF, 0x3A6B8B);
    public static final DeferredItem<DeferredSpawnEggItem> HOLLOW_KING_SPAWN_EGG = egg("hollow_king_spawn_egg", ModEntities.HOLLOW_KING::get, 0x1A0B2E, 0xB04FFF);

    // Block items
    public static final DeferredItem<BlockItem> VEILSTONE_ITEM = blockItem("veilstone", ModBlocks.VEILSTONE);
    public static final DeferredItem<BlockItem> COBBLED_VEILSTONE_ITEM = blockItem("cobbled_veilstone", ModBlocks.COBBLED_VEILSTONE);
    public static final DeferredItem<BlockItem> VEILSTONE_BRICKS_ITEM = blockItem("veilstone_bricks", ModBlocks.VEILSTONE_BRICKS);
    public static final DeferredItem<BlockItem> CHISELED_VEILSTONE_ITEM = blockItem("chiseled_veilstone", ModBlocks.CHISELED_VEILSTONE);
    public static final DeferredItem<BlockItem> VEILSTONE_STAIRS_ITEM = blockItem("veilstone_stairs", ModBlocks.VEILSTONE_STAIRS);
    public static final DeferredItem<BlockItem> VEILSTONE_SLAB_ITEM = blockItem("veilstone_slab", ModBlocks.VEILSTONE_SLAB);
    public static final DeferredItem<BlockItem> VEILSTONE_WALL_ITEM = blockItem("veilstone_wall", ModBlocks.VEILSTONE_WALL);
    public static final DeferredItem<BlockItem> VEIL_TURF_ITEM = blockItem("veil_turf", ModBlocks.VEIL_TURF);
    public static final DeferredItem<BlockItem> VOIDSTONE_ITEM = blockItem("voidstone", ModBlocks.VOIDSTONE);
    public static final DeferredItem<BlockItem> VOIDSTONE_BRICKS_ITEM = blockItem("voidstone_bricks", ModBlocks.VOIDSTONE_BRICKS);
    public static final DeferredItem<BlockItem> AETHERITE_ORE_ITEM = blockItem("aetherite_ore", ModBlocks.AETHERITE_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_AETHERITE_ORE_ITEM = blockItem("deepslate_aetherite_ore", ModBlocks.DEEPSLATE_AETHERITE_ORE);
    public static final DeferredItem<BlockItem> VOIDSTONE_ORE_ITEM = blockItem("voidstone_ore", ModBlocks.VOIDSTONE_ORE);
    public static final DeferredItem<BlockItem> GLIMMER_SHARD_ORE_ITEM = blockItem("glimmer_shard_ore", ModBlocks.GLIMMER_SHARD_ORE);
    public static final DeferredItem<BlockItem> GLIMMER_CRYSTAL_BLOCK_ITEM = blockItem("glimmer_crystal_block", ModBlocks.GLIMMER_CRYSTAL_BLOCK);
    public static final DeferredItem<BlockItem> RAW_AETHERITE_BLOCK_ITEM = blockItem("raw_aetherite_block", ModBlocks.RAW_AETHERITE_BLOCK);
    public static final DeferredItem<BlockItem> RAW_VOIDSTONE_BLOCK_ITEM = blockItem("raw_voidstone_block", ModBlocks.RAW_VOIDSTONE_BLOCK);
    public static final DeferredItem<BlockItem> VEIL_PORTAL_FRAME_ITEM = blockItem("veil_portal_frame", ModBlocks.VEIL_PORTAL_FRAME);
    public static final DeferredItem<BlockItem> ABYSS_STONE_ITEM = blockItem("abyss_stone", ModBlocks.ABYSS_STONE);
    public static final DeferredItem<BlockItem> ABYSS_PORTAL_FRAME_ITEM = blockItem("abyss_portal_frame", ModBlocks.ABYSS_PORTAL_FRAME);
    public static final DeferredItem<BlockItem> GRAVITY_ANCHOR_ITEM = blockItem("gravity_anchor", ModBlocks.GRAVITY_ANCHOR);
    public static final DeferredItem<BlockItem> RESONANT_SPIRE_ITEM = blockItem("resonant_spire", ModBlocks.RESONANT_SPIRE);
    public static final DeferredItem<BlockItem> EMBER_BRAZIER_ITEM = blockItem("ember_brazier", ModBlocks.EMBER_BRAZIER);
    public static final DeferredItem<BlockItem> VEIL_GRASS_ITEM = blockItem("veil_grass", ModBlocks.VEIL_GRASS);
    public static final DeferredItem<BlockItem> VEIL_BLOOM_ITEM = blockItem("veil_bloom", ModBlocks.VEIL_BLOOM);
    public static final DeferredItem<BlockItem> HANGING_VEIL_VINES_ITEM = blockItem("hanging_veil_vines", ModBlocks.HANGING_VEIL_VINES);
    public static final DeferredItem<BlockItem> GLOWING_VEIL_FUNGUS_ITEM = blockItem("glowing_veil_fungus", ModBlocks.GLOWING_VEIL_FUNGUS);

    private static DeferredItem<Item> simple(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    private static <T extends Item> DeferredItem<T> tool(String name, Tier tier, java.util.function.BiFunction<Tier, Item.Properties, T> factory) {
        return ITEMS.registerItem(name, p -> factory.apply(tier, p.durability(tier.getUses())));
    }

    private static DeferredItem<ArmorItem> armor(String name,
            net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.ArmorMaterial, net.minecraft.world.item.ArmorMaterial> material,
            ArmorItem.Type type, int durability) {
        return ITEMS.registerItem(name, p -> new ArmorItem(material, type, p.durability(type.getDurability(durability))));
    }

    private static DeferredItem<DeferredSpawnEggItem> egg(String name,
            java.util.function.Supplier<net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> type,
            int background, int highlight) {
        return ITEMS.registerItem(name, p -> new DeferredSpawnEggItem(type, background, highlight, p));
    }

    private static DeferredItem<BlockItem> blockItem(String name, net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.level.block.Block, ? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }
}
