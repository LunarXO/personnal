package com.realmwalker.event;

import com.realmwalker.ModDimensions;
import com.realmwalker.Realmwalker;
import com.realmwalker.RealmwalkerConfig;
import com.realmwalker.block.LightningRodBlock;
import com.realmwalker.block.entity.WaystonePortalBlockEntity;
import com.realmwalker.item.RealmwalkerTeleporter;
import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModEntities;
import com.realmwalker.registry.ModItems;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Realmwalker.MODID)
public class CommonEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation dim = entity.level().dimension().location();
        int index = ModDimensions.indexOf(entity.level().dimension());
        if (index >= 0) {
            QuestTracker.add(player, dim.getPath() + "_mob_kills", 1);
        }
        EntityType<?> type = entity.getType();
        if (type == ModEntities.MIRAGE_PHANTOM.get()) {
            QuestTracker.add(player, "saltglass_phantom_kills", 1);
        } else if (type == ModEntities.RIME_WOLF.get()) {
            QuestTracker.add(player, "frostmarrow_wolf_kills", 1);
        } else if (type == ModEntities.ROD_KNIGHT.get()) {
            QuestTracker.add(player, "stormspire_knight_kills", 1);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || event.getLevel() instanceof Level level && level.isClientSide) {
            return;
        }
        if (event.getState().is(ModBlocks.AMBER_CRYSTAL.get()) && event.getLevel().dimension().equals(ModDimensions.SALTGLASS_EXPANSE)) {
            QuestTracker.add(player, "saltglass_crystals", 1);
        }
    }

    private static final String[] BOSS_ADVANCEMENTS = {
            "realmwalker:lumenfen_boss", "realmwalker:saltglass_expanse_boss", "realmwalker:cogspire_boss",
            "realmwalker:frostmarrow_boss", "realmwalker:stormspire_boss"
    };

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        ServerPlayer player = event.getEntity();
        ResourceLocation id = event.getAdvancement().id();
        if (id.getNamespace().equals(Realmwalker.MODID) && id.getPath().equals("ascension")) {
            grantAscensionRewards(player);
            return;
        }
        if (id.getNamespace().equals(Realmwalker.MODID) && id.getPath().endsWith("_boss")) {
            boolean all = true;
            for (String bossAdv : BOSS_ADVANCEMENTS) {
                var holder = player.server.getAdvancements().get(ResourceLocation.parse(bossAdv));
                if (holder == null || !player.getAdvancements().getOrStartProgress(holder).isDone()) {
                    all = false;
                    break;
                }
            }
            if (all) {
                QuestTracker.fire(player, "ascension");
            }
        }
    }

    private static void grantAscensionRewards(ServerPlayer player) {
        if (player.getPersistentData().getBoolean("realmwalker:ascension_rewarded")) {
            return;
        }
        player.getPersistentData().putBoolean("realmwalker:ascension_rewarded", true);
        player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.WAYFARERS_MANTLE.get()));
        player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.SIGIL_OF_FIVE.get()));
        player.displayClientMessage(Component.translatable("message.realmwalker.ascension"), false);
        player.level().playSound(null, player.blockPosition(), ModSoundEvents.QUEST_COMPLETE.get(), SoundSource.PLAYERS, 1.0F, 0.6F);
        buildEverbloomPortal(player);
    }

    private static void buildEverbloomPortal(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }
        BlockPos spawn = player.getRespawnPosition() != null ? player.getRespawnPosition() : overworld.getSharedSpawnPos();
        if (spawn == null) {
            spawn = overworld.getSharedSpawnPos();
        }
        BlockPos ground = overworld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn);
        BlockPos origin = ground;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 5; y++) {
                BlockPos p = origin.offset(x, y, 0);
                boolean border = x == 0 || x == 3 || y == 0 || y == 4;
                if (border) {
                    overworld.setBlockAndUpdate(p, ModBlocks.WAYSTONE_BRICKS.get().defaultBlockState());
                } else {
                    overworld.setBlockAndUpdate(p, ModBlocks.WAYSTONE_PORTAL.get().defaultBlockState());
                    if (overworld.getBlockEntity(p) instanceof WaystonePortalBlockEntity portal) {
                        portal.setTarget(ModDimensions.EVERBLOOM.location().toString());
                        portal.setReturnDim(Level.OVERWORLD.location().toString());
                    }
                }
            }
        }
        player.displayClientMessage(Component.translatable("message.realmwalker.everbloom_portal"), false);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            tickSigil(serverPlayer);
        }
        if (RealmwalkerConfig.enableColdMeter && player.level().dimension().equals(ModDimensions.FROSTMARROW)) {
            tickColdMeter(player);
        }
    }

    private static void tickSigil(ServerPlayer player) {
        if (player.getMainHandItem().is(ModItems.SIGIL_OF_FIVE.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, false, false, false));
        }
    }

    private static void tickColdMeter(Player player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = player.blockPosition();
        boolean blizzard = level.isRaining() && level.canSeeSky(pos);
        boolean tonic = player.getPersistentData().getInt("realmwalker:tonic_ticks") > 0;
        if (tonic) {
            player.getPersistentData().putInt("realmwalker:tonic_ticks", player.getPersistentData().getInt("realmwalker:tonic_ticks") - 20);
        }
        boolean protectedFromCold = tonic
                || player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(ModItems.FURRED_PARKA.get())
                || isNearWarmth(level, pos);
        if (blizzard && !protectedFromCold) {
            int frozen = Math.min(player.getTicksFrozen() + 4, 300);
            player.setTicksFrozen(frozen);
            if (frozen >= 140 && player.tickCount % 40 == 0) {
                player.hurt(player.damageSources().freeze(), 1.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, false, false));
            }
        } else {
            player.setTicksFrozen(Math.max(player.getTicksFrozen() - 5, 0));
        }
    }

    private static boolean isNearWarmth(ServerLevel level, BlockPos center) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    var state = level.getBlockState(p);
                    if (state.getLightEmission() >= 11 || state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)
                            || state.is(Blocks.LAVA) || state.is(Blocks.FIRE) || state.is(Blocks.MAGMA_BLOCK)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % 60 != 0) {
            return;
        }
        if (level.dimension().equals(ModDimensions.STORMSPIRE)) {
            tickStormspire(level);
        }
        if (level.dimension().equals(ModDimensions.SALTGLASS_EXPANSE) && level.getDayTime() % 24000L == 13000L) {
            for (ServerPlayer player : level.players()) {
                QuestTracker.fire(player, "saltglass_first_night");
            }
        }
    }

    private static void tickStormspire(ServerLevel level) {
        // Permanent thunderstorm.
        if (!level.isThundering()) {
            level.setWeatherParameters(0, 12000, true, true);
        }
        if (level.getGameTime() % 100 != 0 || level.players().isEmpty()) {
            return;
        }
        Player player = level.players().get(level.getRandom().nextInt(level.players().size()));
        BlockPos center = player.blockPosition();
        int dx = level.getRandom().nextInt(49) - 24;
        int dz = level.getRandom().nextInt(49) - 24;
        BlockPos strike = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, center.offset(dx, 0, dz));
        if (strike.getY() <= level.getMinBuildHeight()) {
            return;
        }
        // A Lightning Rod nearby redirects the strike.
        BlockPos rod = findRod(level, strike);
        if (rod != null) {
            strike = rod;
            if (level.getBlockState(rod).getBlock() instanceof LightningRodBlock rodBlock) {
                rodBlock.setPowered(level, rod, true);
            }
            level.playSound(null, rod, ModSoundEvents.LIGHTNING_CHARGE.get(), SoundSource.WEATHER, 2.0F, 1.0F);
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(strike.getX() + 0.5D, strike.getY(), strike.getZ() + 0.5D);
            bolt.setVisualOnly(false);
            level.addFreshEntity(bolt);
        }
    }

    private static BlockPos findRod(ServerLevel level, BlockPos center) {
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (level.getBlockState(p).is(ModBlocks.LIGHTNING_ROD.get())) {
                        return p;
                    }
                }
            }
        }
        return null;
    }
}
