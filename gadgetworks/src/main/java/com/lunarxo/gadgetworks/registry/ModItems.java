package com.lunarxo.gadgetworks.registry;

import com.lunarxo.gadgetworks.Gadgetworks;
import com.lunarxo.gadgetworks.item.CopperBootsItem;
import com.lunarxo.gadgetworks.item.ElectricSwordItem;
import com.lunarxo.gadgetworks.item.GrapplingHookItem;
import com.lunarxo.gadgetworks.item.GroundBreakingSpearItem;
import com.lunarxo.gadgetworks.item.MagneticDoubleSwordItem;
import com.lunarxo.gadgetworks.item.RedstoneStaffItem;
import com.lunarxo.gadgetworks.item.RedstoneSparkItem;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gadgetworks.MODID);

    public static final DeferredItem<CopperBootsItem> COPPER_BOOTS = ITEMS.register("copper_boots",
            CopperBootsItem::new);

    public static final DeferredItem<GrapplingHookItem> GRAPPLING_HOOK = ITEMS.register("grappling_hook",
            GrapplingHookItem::new);

    public static final DeferredItem<ElectricSwordItem> ELECTRIC_SWORD = ITEMS.register("electric_sword",
            ElectricSwordItem::new);

    public static final DeferredItem<SwordItem> MAGNETIC_DOUBLE_SWORD = ITEMS.register("magnetic_double_sword",
            MagneticDoubleSwordItem::new);

    public static final DeferredItem<SwordItem> GROUND_BREAKING_SPEAR = ITEMS.register("ground_breaking_spear",
            GroundBreakingSpearItem::new);

    public static final DeferredItem<RedstoneStaffItem> REDSTONE_STAFF = ITEMS.register("redstone_staff",
            RedstoneStaffItem::new);

    public static final DeferredItem<RedstoneSparkItem> REDSTONE_SPARK = ITEMS.register("redstone_spark",
            RedstoneSparkItem::new);

    public static final DeferredItem<Item> MINI_REDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("mini_redstone_golem_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MINI_REDSTONE_GOLEM,
                    0xB48873, 0x8C1D1D, new Item.Properties()));
}
