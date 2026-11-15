package com.realmwalker.item;

import com.realmwalker.quest.QuestTracker;
import com.realmwalker.registry.ModBlocks;
import com.realmwalker.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** The Pressure Gauge: reveals the nearest steam source in Cogspire and counts it toward the quest. */
public class PressureGaugeItem extends Item {
    public PressureGaugeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockPos source = findNearestSteamVent(level, player.blockPosition(), 48);
            if (source != null) {
                QuestTracker.add(serverPlayer, "cogspire_steam_sources", 1);
                player.displayClientMessage(Component.translatable("message.realmwalker.steam_found", source.getX(), source.getY(), source.getZ()), true);
                level.playSound(null, player.blockPosition(), ModSoundEvents.SHRINE_LOOT.get(), SoundSource.PLAYERS, 1.0F, 1.1F);
            } else {
                player.displayClientMessage(Component.translatable("message.realmwalker.steam_none"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static BlockPos findNearestSteamVent(Level level, BlockPos center, int radius) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx += 4) {
            for (int dy = -radius; dy <= radius; dy += 4) {
                for (int dz = -radius; dz <= radius; dz += 4) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (level.getBlockState(p).is(ModBlocks.STEAM_VENT.get())) {
                        double dist = p.distSqr(center);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = p;
                        }
                    }
                }
            }
        }
        return best;
    }
}
