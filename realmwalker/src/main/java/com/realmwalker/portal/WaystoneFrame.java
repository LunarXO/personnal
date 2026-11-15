package com.realmwalker.portal;

import com.realmwalker.ModDimensions;
import com.realmwalker.Realmwalker;
import com.realmwalker.RealmwalkerConfig;
import com.realmwalker.block.entity.WaystonePortalBlockEntity;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Detects and lights a Waystone portal: a 4 wide x 5 high frame of Waystone Bricks with a 2x3 air interior.
 */
public final class WaystoneFrame {
    public static final int WIDTH = 4;
    public static final int HEIGHT = 5;

    private WaystoneFrame() {
    }

    public static boolean isFrameBlock(BlockState state) {
        return state.is(ModBlocks.WAYSTONE_BRICKS.get());
    }

    /** Returns the frame origin (lowest corner) if the block at pos belongs to a valid unlit frame, else null. */
    public static BlockPos findFrameOrigin(Level level, BlockPos pos) {
        for (int dx = -(WIDTH - 1); dx <= 0; dx++) {
            for (int dy = -(HEIGHT - 1); dy <= 0; dy++) {
                BlockPos origin = pos.offset(dx, dy, 0);
                if (isValidFrame(level, origin)) {
                    return origin;
                }
            }
        }
        return null;
    }

    public static boolean isValidFrame(Level level, BlockPos origin) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                BlockPos p = origin.offset(x, y, 0);
                boolean border = x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1;
                if (border) {
                    if (!isFrameBlock(level.getBlockState(p))) {
                        return false;
                    }
                } else if (!level.getBlockState(p).isAir() && !level.getBlockState(p).is(ModBlocks.WAYSTONE_PORTAL.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean canLight(Player player, ResourceKey<Level> target) {
        int index = ModDimensions.indexOf(target);
        if (index <= 0 || !RealmwalkerConfig.requirePreviousBoss) {
            return true;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }
        ResourceKey<Level> previous = ModDimensions.ADVENTURE_DIMS[index - 1];
        String advancement = "realmwalker:" + previous.location().getPath() + "_boss";
        var holder = serverPlayer.server.getAdvancements().get(ResourceLocation.parse(advancement));
        return holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
    }

    public static boolean light(Level level, BlockPos clicked, Player player, ResourceKey<Level> target) {
        if (!isFrameBlock(level.getBlockState(clicked))) {
            return false;
        }
        BlockPos origin = findFrameOrigin(level, clicked);
        if (origin == null) {
            return false;
        }
        if (!canLight(player, target)) {
            if (player != null) {
                player.displayClientMessage(Component.translatable("message.realmwalker.portal_locked"), true);
            }
            return false;
        }
        if (level.isClientSide) {
            return true;
        }
        String returnDim = level.dimension().location().toString();
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                BlockPos p = origin.offset(x, y, 0);
                level.setBlockAndUpdate(p, ModBlocks.WAYSTONE_PORTAL.get().defaultBlockState());
                if (level.getBlockEntity(p) instanceof WaystonePortalBlockEntity portal) {
                    portal.setTarget(target.location().toString());
                    portal.setReturnDim(returnDim);
                }
            }
        }
        level.playSound(null, origin.offset(1, 2, 0), ModSoundEvents.PORTAL_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        Realmwalker.LOGGER.debug("Lit waystone portal to {} at {}", target.location(), origin);
        return true;
    }
}
