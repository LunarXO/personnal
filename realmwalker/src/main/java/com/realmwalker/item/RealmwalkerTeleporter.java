package com.realmwalker.item;

import com.realmwalker.ModDimensions;
import com.realmwalker.block.entity.WaystonePortalBlockEntity;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Set;

public final class RealmwalkerTeleporter {
    private static final int FRAME_W = 4;
    private static final int FRAME_H = 5;

    private RealmwalkerTeleporter() {
    }

    public static void teleport(Entity entity, ResourceKey<Level> target) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        ServerLevel dest = level.getServer().getLevel(target);
        if (dest == null || dest == level) {
            return;
        }
        ResourceKey<Level> origin = level.dimension();
        entity.getPersistentData().putString("realmwalker:origin_dim", origin.location().toString());
        if (origin.equals(Level.OVERWORLD)) {
            entity.getPersistentData().putLong("realmwalker:last_overworld_x", entity.blockPosition().getX());
            entity.getPersistentData().putLong("realmwalker:last_overworld_z", entity.blockPosition().getZ());
        }

        BlockPos arrival = computeArrivalPos(dest, entity);
        ensureArrivalPlatform(dest, arrival, origin);

        double tx = arrival.getX() + 0.5D;
        double ty = arrival.getY() + 1.0D;
        double tz = arrival.getZ() + 0.5D;
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(dest, tx, ty, tz, yaw, pitch);
        } else {
            entity.teleportTo(dest, tx, ty, tz, Set.of(RelativeMovement.X, RelativeMovement.Z), yaw, pitch);
        }
        dest.playSound(null, tx, ty, tz, ModSoundEvents.PORTAL_TRAVEL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static BlockPos computeArrivalPos(ServerLevel dest, Entity entity) {
        if (ModDimensions.isAdventureDim(dest.dimension()) || dest.dimension().equals(ModDimensions.EVERBLOOM)) {
            // Fixed arrival platform near the dimension's origin so every visitor lands in the same place.
            BlockPos base = new BlockPos(8, 0, 8);
            dest.getChunk(SectionPos.blockToSectionCoord(base.getX()), SectionPos.blockToSectionCoord(base.getZ()));
            return dest.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);
        }
        long x = entity.getPersistentData().getLong("realmwalker:last_overworld_x");
        long z = entity.getPersistentData().getLong("realmwalker:last_overworld_z");
        BlockPos pos;
        if (x == 0 && z == 0) {
            pos = dest.getSharedSpawnPos();
        } else {
            pos = new BlockPos((int) x, 0, (int) z);
        }
        dest.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        BlockPos top = dest.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        if (top.getY() <= dest.getMinBuildHeight()) {
            return dest.getSharedSpawnPos().above(2);
        }
        return top;
    }

    /** Builds the Waystone arrival platform and a return portal at the arrival position. */
    public static void ensureArrivalPlatform(ServerLevel level, BlockPos ground, ResourceKey<Level> returnDim) {
        int y = ground.getY();
        // 5x5 platform of waystone bricks with the return portal frame on top.
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlockAndUpdate(new BlockPos(ground.getX() + dx, y, ground.getZ() + dz), ModBlocks.WAYSTONE_BRICKS.get().defaultBlockState());
            }
        }
        BlockPos frameOrigin = new BlockPos(ground.getX() - 1, y + 1, ground.getZ() - 2);
        for (int x = 0; x < FRAME_W; x++) {
            for (int h = 0; h < FRAME_H; h++) {
                BlockPos p = frameOrigin.offset(x, h, 0);
                boolean border = x == 0 || x == FRAME_W - 1 || h == 0 || h == FRAME_H - 1;
                if (border) {
                    level.setBlockAndUpdate(p, ModBlocks.WAYSTONE_BRICKS.get().defaultBlockState());
                } else {
                    level.setBlockAndUpdate(p, ModBlocks.WAYSTONE_PORTAL.get().defaultBlockState());
                    if (level.getBlockEntity(p) instanceof WaystonePortalBlockEntity portal) {
                        portal.setTarget(returnDim.location().toString());
                        portal.setReturnDim(returnDim.location().toString());
                    }
                }
            }
        }
        // Clear space above the platform.
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int h = 1; h <= 4; h++) {
                    BlockPos p = new BlockPos(ground.getX() + dx, y + h, ground.getZ() + dz);
                    if (!level.getBlockState(p).is(ModBlocks.WAYSTONE_BRICKS.get()) && !level.getBlockState(p).is(ModBlocks.WAYSTONE_PORTAL.get())) {
                        level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }

}
