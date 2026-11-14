package com.lunarxo.veilbound.item;

import com.lunarxo.veilbound.ModDimensions;
import com.lunarxo.veilbound.registry.ModParticles;
import com.lunarxo.veilbound.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Set;

public final class VeilTeleporter {
    private VeilTeleporter() {
    }

    public static void teleport(Entity entity, ResourceKey<Level> dimension, ResourceKey<Level> returnDimension) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        ResourceKey<Level> targetKey = level.dimension().equals(dimension) ? returnDimension : dimension;
        ServerLevel target = level.getServer().getLevel(targetKey);
        if (target == null || target == level) {
            return;
        }
        int x = (int) Math.floor(entity.getX());
        int z = (int) Math.floor(entity.getZ());
        BlockPos safe = findSafePos(target, x, z);
        double tx = safe.getX() + 0.5D;
        double ty = safe.getY() + 1.0D;
        double tz = safe.getZ() + 0.5D;
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(target, tx, ty, tz, yaw, pitch);
        } else {
            entity.teleportTo(target, tx, ty, tz, Set.of(RelativeMovement.X, RelativeMovement.Z), yaw, pitch);
        }
        target.playSound(null, tx, ty, tz, ModSoundEvents.PORTAL_TRAVEL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        if (target instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((ParticleOptions) ModParticles.SHARD_SPARKLE.get(), tx, ty + 1.0D, tz, 30, 0.5D, 1.0D, 0.5D, 0.02D);
        }
    }

    private static BlockPos findSafePos(ServerLevel level, int x, int z) {
        for (int radius = 0; radius <= 256; radius += 8) {
            for (int dx = -radius; dx <= radius; dx += 8) {
                for (int dz = -radius; dz <= radius; dz += 8) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    BlockPos column = new BlockPos(x + dx, 0, z + dz);
                    BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
                    if (top.getY() <= level.getMinBuildHeight()) {
                        continue;
                    }
                    if (!level.getBlockState(top.below()).isAir() && level.getBlockState(top).isAir()
                            && level.getBlockState(top.above()).isAir()) {
                        return top;
                    }
                }
            }
        }
        return new BlockPos(x, 80, z);
    }
}
