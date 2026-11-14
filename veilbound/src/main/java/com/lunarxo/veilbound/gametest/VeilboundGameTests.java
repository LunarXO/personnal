package com.lunarxo.veilbound.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lunarxo.veilbound.Veilbound;
import com.lunarxo.veilbound.registry.ModBlocks;
import com.lunarxo.veilbound.registry.ModEntities;
import com.lunarxo.veilbound.registry.ModItems;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Veilbound.MODID)
@PrefixGameTestTemplate(false)
public class VeilboundGameTests {
    @GameTest(template = "empty3", timeoutTicks = 200)
    public static void hollowKingSpawns(GameTestHelper helper) {
        helper.spawn(ModEntities.HOLLOW_KING.get(), 1, 2, 1);
        helper.assertEntityPresent(ModEntities.HOLLOW_KING.get(), 1, 2, 1);
        helper.succeed();
    }

    @GameTest(template = "empty3", timeoutTicks = 200)
    public static void portalIgnitesWithKey(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.VEIL_PORTAL_FRAME.get());
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.VEIL_KEY.get()));
        BlockPos abs = helper.absolutePos(pos);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        ModItems.VEIL_KEY.get().useOn(context);
        helper.assertBlockPresent(ModBlocks.VEIL_PORTAL.get(), pos);
        helper.succeed();
    }

    @GameTest(template = "empty3", timeoutTicks = 200)
    public static void abyssPortalIgnitesWithKey(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.ABYSS_PORTAL_FRAME.get());
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.ABYSS_KEY.get()));
        BlockPos abs = helper.absolutePos(pos);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false));
        ModItems.ABYSS_KEY.get().useOn(context);
        helper.assertBlockPresent(ModBlocks.ABYSS_PORTAL.get(), pos);
        helper.succeed();
    }

    @GameTest(template = "empty3", timeoutTicks = 100)
    public static void dimensionsRegistered(GameTestHelper helper) {
        // GameTestServer bakes the flat world preset, so custom level stems are filtered out of
        // the runtime LEVEL_STEM registry (NeoForge only injects datapack dimensions into normal
        // worlds). Instead verify the dimension datapack files ship in the pack and fully parse
        // as level stems against the server's worldgen registries.
        MinecraftServer server = helper.getLevel().getServer();
        var ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        for (String dim : new String[]{"the_veil", "the_depths"}) {
            ResourceLocation file = ResourceLocation.fromNamespaceAndPath(Veilbound.MODID, "dimension/" + dim + ".json");
            helper.assertTrue(server.getResourceManager().getResource(file).isPresent(), dim + " dimension file missing from datapack");
            JsonElement json;
            try (var reader = server.getResourceManager().getResourceOrThrow(file).openAsReader()) {
                json = JsonParser.parseReader(reader);
            } catch (Exception e) {
                throw new GameTestAssertException(dim + " dimension file unreadable: " + e);
            }
            helper.assertTrue(json.isJsonObject() && json.getAsJsonObject().has("type") && json.getAsJsonObject().has("generator"),
                    dim + " dimension json missing type/generator");
            var parsed = LevelStem.CODEC.parse(ops, json);
            helper.assertTrue(parsed.result().isPresent(),
                    dim + " failed to parse as level stem: " + parsed.error().map(Object::toString).orElse("unknown"));
            String typeId = json.getAsJsonObject().get("type").getAsString();
            helper.assertTrue(server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE)
                    .containsKey(ResourceLocation.parse(typeId)), dim + " dimension_type missing: " + typeId);
        }
        helper.succeed();
    }

    @GameTest(template = "empty3", timeoutTicks = 100)
    public static void structureRegistered(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        boolean found = helper.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE).containsKey(Veilbound.id("veil_ruin"));
        helper.assertTrue(found, "veil_ruin structure missing");
        helper.assertTrue(server.getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(Veilbound.MODID, "structure/veil_ruin.nbt")).isPresent(),
                "veil_ruin.nbt template missing from datapack");
        helper.succeed();
    }

    @GameTest(template = "empty3", timeoutTicks = 100)
    public static void weaponsAndProjectilesRegistered(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(Veilbound.id("storm_scepter")), "storm_scepter missing");
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(Veilbound.id("ember_staff")), "ember_staff missing");
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(Veilbound.id("abyss_key")), "abyss_key missing");
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.containsKey(Veilbound.id("storm_bolt")), "storm_bolt missing");
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.containsKey(Veilbound.id("fireball")), "fireball missing");
        helper.succeed();
    }
}
